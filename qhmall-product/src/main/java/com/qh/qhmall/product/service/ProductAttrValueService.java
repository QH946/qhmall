package com.qh.qhmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存SPU的规格参数
     *
     * @param collect 收集
     */
    void saveProductAttr(List<ProductAttrValueEntity> collect);


    /**
     * 获取SPU规格
     *
     * @param spuId spu id
     * @return {@link List}<{@link ProductAttrValueEntity}>
     */
    List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

    /**
     * 修改商品规格
     *
     * @param spuId    spu id
     * @param entities 实体
     */
    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);
}

