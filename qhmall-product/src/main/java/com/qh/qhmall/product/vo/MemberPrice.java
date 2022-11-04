package com.qh.qhmall.product.vo;

import lombok.Data;

import java.math.BigDecimal;


/**
 * 会员价格视图对象
 *
 * @author qh
 * @date 2022/11/04 15:18:15
 */
@Data
public class MemberPrice {

    private Long id;
    private String name;
    private BigDecimal price;

}