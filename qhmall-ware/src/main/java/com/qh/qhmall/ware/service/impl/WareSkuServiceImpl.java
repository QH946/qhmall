package com.qh.qhmall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.to.SkuHasStockVo;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.common.utils.R;
import com.qh.qhmall.ware.dao.WareSkuDao;
import com.qh.qhmall.ware.entity.WareSkuEntity;
import com.qh.qhmall.ware.feign.ProductFeignService;
import com.qh.qhmall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private ProductFeignService productFeignService;


    /**
     * 查询库存
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        // 取出请求的参数 组装条件进行查询
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    /**
     * 将成功采购的进行入库
     *
     * @param skuId  sku id
     * @param wareId 器皿id
     * @param skuNum sku num
     */
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //先根据 skuId 和 ware_id 查询是否拥有这个用户
        List<WareSkuEntity> wareSkuEntities =
                wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>()
                        .eq("sku_id", skuId)
                        .eq("ware_id", wareId));
        //没有这个用户就新增
        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            //根据属性值设置
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            //TODO 还可以用什么办法让异常出现以后不回滚，高级篇优化
            try {
                // 远程调用 根据 skuid进行查询
                R info = productFeignService.info(skuId);
                Map<String, Object> map = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    wareSkuEntity.setSkuName((String) map.get("skuName"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            wareSkuDao.insert(wareSkuEntity);
        } else {
            //有该记录就进行更新
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    /**
     * 查询SKU是否有库存
     *
     * @param skuIds sku id
     * @return {@link List}<{@link SkuHasStockVo}>
     */
    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(item -> {
            Long count = baseMapper.getSkuStock(item);
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setSkuId(item);
            skuHasStockVo.setHasStock(count != null && count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
    }
}