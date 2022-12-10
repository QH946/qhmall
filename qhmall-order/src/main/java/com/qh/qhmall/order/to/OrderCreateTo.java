package com.qh.qhmall.order.to;


import com.qh.qhmall.order.entity.OrderEntity;
import com.qh.qhmall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 订单创建
 *
 * @author 清欢
 * @date 2022/12/03  17:03:52
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    /**
     * 订单计算的应付价格
     */
    private BigDecimal payPrice;
    /**
     * 运费
     */
    private BigDecimal fare;
}
