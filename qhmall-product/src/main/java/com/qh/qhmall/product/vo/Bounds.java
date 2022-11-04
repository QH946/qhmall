package com.qh.qhmall.product.vo;

import lombok.Data;

import java.math.BigDecimal;


/**
 * 积分视图对象
 *
 * @author qh
 * @date 2022/11/04 15:17:30
 */
@Data
public class Bounds {

    /**
     * 购物积分
     */
    private BigDecimal buyBounds;
    /**
     * 成长积分
     */
    private BigDecimal growBounds;


}