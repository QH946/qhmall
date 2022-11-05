package com.qh.qhmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.ware.entity.PurchaseEntity;
import com.qh.qhmall.ware.vo.MergeVo;
import com.qh.qhmall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:33:15
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取所有未领取的采购单
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    /**
     * 合并采购需求
     *
     * @param mergeVo 合并签证官
     */
    void mergePurchase(MergeVo mergeVo);

    /**
     * 领取采购单
     *
     * @param ids id
     */
    void received(List<Long> ids);

    /**
     * 完成采购单
     *
     * @param doneVo 做签证官
     */
    void done(PurchaseDoneVo doneVo);
}

