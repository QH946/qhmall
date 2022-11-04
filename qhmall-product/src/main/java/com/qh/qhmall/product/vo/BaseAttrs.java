package com.qh.qhmall.product.vo;

import lombok.Data;


/**
 * 基本属性视图对象
 *
 * @author qh
 * @date 2022/11/04 15:17:05
 */
@Data
public class BaseAttrs {
    private Long attrId;
    private String attrValues;
    private int showDesc;
}