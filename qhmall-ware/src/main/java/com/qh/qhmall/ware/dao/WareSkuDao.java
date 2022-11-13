package com.qh.qhmall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qh.qhmall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:33:15
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    /**
     * 将成功采购的进行入库
     *
     * @param skuId  sku id
     * @param wareId 器皿id
     * @param skuNum sku num
     */
    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    /**
     * 获取SKU库存
     *
     * @param skuId sku id
     * @return {@link Long}
     */
    Long getSkuStock(@Param("skuId") Long skuId);
}
