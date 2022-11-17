package com.qh.qhmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.product.entity.SkuInfoEntity;
import com.qh.qhmall.product.vo.SkuItemVo;

import java.util.List;
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

    /**
     * 查询当前spuId对应的sku信息
     *
     * @param spuId spu id
     * @return {@link List}<{@link SkuInfoEntity}>
     */
    List<SkuInfoEntity> getSkuById(Long spuId);

    /**
     * 展示当前sku的详情
     *
     * @param skuId sku id
     * @return {@link SkuItemVo}
     */
    SkuItemVo item(Long skuId);
}

