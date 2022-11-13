package com.qh.qhmall.ware.vo;


import lombok.Data;

/**
 * SKU有库存
 *
 * @author 清欢
 * @date 2022/11/13 14:51:03
 */
@Data
public class SkuHasStockVo {

    private Long skuId;

    private Boolean hasStock;

}
