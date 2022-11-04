package com.qh.qhmall.product.vo;

import lombok.Data;

/**
 * 获取分类关联的品牌时的品牌视图对象
 *
 * @author qh
 * @date 2022/11/04 14:10:47
 */
@Data
public class BrandVo {

    /**
     * "brandId": 0,
     * "brandName": "string",
     */
    private Long brandId;
    private String brandName;
}
