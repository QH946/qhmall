package com.qh.common.to;

import lombok.Data;


/**
 * SKU是否有库存传输对象
 *
 * @author 清欢
 * @date 2022/11/13 15:09:49
 */
@Data
public class SkuHasStockVo {

    private Long skuId;

    private Boolean hasStock;

}
