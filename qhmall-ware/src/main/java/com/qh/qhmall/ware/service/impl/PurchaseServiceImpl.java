package com.qh.qhmall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.constant.WareConstant;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.qhmall.ware.dao.PurchaseDao;
import com.qh.qhmall.ware.entity.PurchaseDetailEntity;
import com.qh.qhmall.ware.entity.PurchaseEntity;
import com.qh.qhmall.ware.service.PurchaseDetailService;
import com.qh.qhmall.ware.service.PurchaseService;
import com.qh.qhmall.ware.service.WareSkuService;
import com.qh.qhmall.ware.vo.MergeVo;
import com.qh.qhmall.ware.vo.PurchaseDoneVo;
import com.qh.qhmall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {


    @Autowired
    private PurchaseDetailService detailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取所有未领取的采购单
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
                        .eq("status", 0).or().eq("status", 1)
        );
        return new PageUtils(page);
    }

    /**
     * 合并采购需求
     *
     * @param mergeVo 合并签证官
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            //若没有则新建新的采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED
                    .getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        //TODO 确认采购单状态是0,1才可以合并
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect =
                items.stream().map(i -> {
                    PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                    detailEntity.setId(i);
                    detailEntity.setPurchaseId(finalPurchaseId);
                    detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum
                            .ASSIGNED.getCode());
                    return detailEntity;
                }).collect(Collectors.toList());
        detailService.updateBatchById(collect);
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    /**
     * 领取采购单
     *
     * @param ids id
     */
    @Override
    public void received(List<Long> ids) {
        //确认当前采购单是新建或者已分配状态,才能进行采购
        List<PurchaseEntity> collect = ids.stream()
                //根据采购id查询出采购信息
                .map(this::getById)
                //新建或已分配的留下
                .filter(item -> {
                    return item.getStatus() == WareConstant.PurchaseStatusEnum
                            .CREATED.getCode() ||
                            item.getStatus() == WareConstant.PurchaseStatusEnum
                                    .ASSIGNED.getCode();
                }).map(item -> {
                    //设置为已领取
                    item.setStatus(WareConstant.PurchaseStatusEnum
                            .RECEIVE.getCode());
                    item.setUpdateTime(new Date());
                    return item;
                }).collect(Collectors.toList());
        //改变采购单的状态
        this.updateBatchById(collect);
        //改变采购项的状态
        collect.forEach((item) -> {
            //根据purchase_id查询出采购需求
            List<PurchaseDetailEntity> entities =
                    detailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntities =
                    entities.stream().map(e -> {
                        PurchaseDetailEntity entity = new PurchaseDetailEntity();
                        entity.setId(e.getId());
                        //设置状态为正在采购
                        entity.setStatus(WareConstant.PurchaseDetailStatusEnum
                                .BUYING.getCode());
                        return entity;
                    }).collect(Collectors.toList());
            detailService.updateBatchById(detailEntities);
        });
    }

    /**
     * 完成采购单
     *
     * @param doneVo 做签证官
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void done(PurchaseDoneVo doneVo) {
        //采购单id
        Long id = doneVo.getId();
        //改变采购项的状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        List<PurchaseDetailEntity> entities = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            //如果采购失败
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum
                    .HASERROR.getCode()) {
                flag = false;
                detailEntity.setStatus(item.getStatus());
            } else {
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum
                        .FINISH.getCode());
                //将成功采购的进行入库
                PurchaseDetailEntity entity = detailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(),
                        entity.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            entities.add(detailEntity);
        }
        detailService.updateBatchById(entities);
        //改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        //设置状态根据变量判断
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum
                .FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

}