package com.qh.qhmall.order.vo;

import lombok.Data;


/**
 * sku库存
 *
 * @author 清欢
 * @date 2022/12/03  17:02:29
 */
@Data
public class SkuStockVo {
    private Long skuId;
    private Boolean hasStock;
}
