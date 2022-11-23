package com.qh.qhmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.product.entity.SkuSaleAttrValueEntity;
import com.qh.qhmall.product.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:48
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取spu的销售属性组合
     *
     * @param spuId spu id
     * @return {@link List}<{@link SkuItemSaleAttrVo}>
     */
    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId);
}

