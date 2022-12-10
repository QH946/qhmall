package com.qh.qhmall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 订单项
 *
 * @author 清欢
 * @date 2022/12/05  10:11:09
 */
@Data
public class OrderItemVo {
    private Long skuId;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;
    private boolean hasStock;
    private BigDecimal weight;
}
