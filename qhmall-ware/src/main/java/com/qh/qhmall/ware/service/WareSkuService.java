package com.qh.qhmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.to.SkuHasStockVo;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:33:15
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    /**
     * 查询库存
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * //将成功采购的进行入库
     *
     * @param skuId  sku id
     * @param wareId 器皿id
     * @param skuNum sku num
     */
    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 查询sku是否有库存
     *
     * @param skuIds sku id
     * @return {@link List}<{@link SkuHasStockVo}>
     */
    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);
}

