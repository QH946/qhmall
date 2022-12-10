package com.qh.qhmall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项内容
 *
 * @author 清欢
 * @date 2022/12/01  16:37:04
 */
@Data
public class CartItemVo {

    private Long skuId;

    private Boolean check = true;

    private String title;

    private String image;

    /**
     * 商品套餐属性
     */
    private List<String> skuAttr;

    private BigDecimal price;

    private Integer count;

    private BigDecimal totalPrice;


    /**
     * 计算当前购物项总价
     *
     * @return {@link BigDecimal}
     */
    public BigDecimal getTotalPrice() {
        //价格乘以数量
        return this.price.multiply(new BigDecimal("" + this.count));
    }


}
