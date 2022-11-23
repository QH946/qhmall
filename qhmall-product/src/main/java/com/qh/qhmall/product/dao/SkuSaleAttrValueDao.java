package com.qh.qhmall.product.dao;

import com.qh.qhmall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qh.qhmall.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:48
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    /**
     * 获取spu的销售属性组合
     *
     * @param spuId spu id
     * @return {@link List}<{@link SkuItemSaleAttrVo}>
     */
    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(@Param("spuId") Long spuId);


}
