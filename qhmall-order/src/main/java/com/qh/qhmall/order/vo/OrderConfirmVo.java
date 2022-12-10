package com.qh.qhmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 订单确认页需要用的数据
 *
 * @author 清欢
 * @date 2022/12/03  17:00:38
 */
@Data
public class OrderConfirmVo {

    //收货地址,ums_member_receive_address表
    List<MemberAddressVo> address;

    //所有选中的购物项
    List<OrderItemVo> items;

    //发票记录

    //优惠券信息
    Integer integeration;

    //BigDecimal total;//订单总额
    //BigDecimal payPrice;//应付价格

    //防重令牌
    String orderToken;
    //库存
    Map<Long, Boolean> stocks;

    public Integer getCount() {
        Integer i = 0;
        if (items != null) {
            for (OrderItemVo item : items) {
                i += item.getCount();
            }
        }
        return i;
    }

    /**
     * 获取订单总额
     *
     * @return {@link BigDecimal}
     */
    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

    /**
     * 获取应付价格
     *
     * @return {@link BigDecimal}
     */
    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
