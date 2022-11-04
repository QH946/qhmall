package com.qh.qhmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:48
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存SKU信息
     *
     * @param skuInfoEntity sku信息实体
     */
    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    /**
     * 查询SKU
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    PageUtils queryPageByCondition(Map<String, Object> params);
}

