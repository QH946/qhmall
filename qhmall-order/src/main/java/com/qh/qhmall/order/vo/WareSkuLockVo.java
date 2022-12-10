package com.qh.qhmall.order.vo;

import lombok.Data;

import java.util.List;


/**
 * 订单锁定库存
 *
 * @author 清欢
 * @date 2022/12/06  21:49:54
 */
@Data
public class WareSkuLockVo {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 需要锁住的所有库存信息
     */
    private List<OrderItemVo> locks;

}
