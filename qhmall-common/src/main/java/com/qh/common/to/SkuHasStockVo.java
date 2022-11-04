package com.qh.common.to;

import lombok.Data;


/**
 * SKU库存传输对象
 *
 * @author qh
 * @date 2022/10/30 18:43:50
 */
@Data
public class SkuHasStockVo {

    private Long skuId;

    private Boolean hasStock;

}
