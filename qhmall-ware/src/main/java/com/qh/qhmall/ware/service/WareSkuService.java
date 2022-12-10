package com.qh.qhmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.to.SkuHasStockVo;
import com.qh.common.to.mq.OrderTo;
import com.qh.common.to.mq.StockLockedTo;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.ware.entity.WareSkuEntity;
import com.qh.qhmall.ware.vo.WareSkuLockVo;

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

    /**
     * 订单锁定库存
     *
     * @param vo 签证官
     * @return {@link Boolean}
     */
    Boolean orderLockStock(WareSkuLockVo vo);

    /**
     * 解锁库存
     *
     * @param to 来
     */
    void unLockStock(StockLockedTo to);

    /**
     * 解锁订单
     *
     * @param orderTo 以
     */
    void unLockStock(OrderTo orderTo);
}

