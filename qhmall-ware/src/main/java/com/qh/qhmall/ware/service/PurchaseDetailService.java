package com.qh.qhmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:33:15
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    /**
     * 查询采购需求
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    PageUtils queryPage(Map<String, Object> params);


    /**
     * 根据采购单id查询采购项
     *
     * @param id id
     * @return {@link List}<{@link PurchaseDetailEntity}>
     */
    List<PurchaseDetailEntity> listDetailByPurchaseId(Long id);
}

