package com.qh.common.to;

import lombok.Data;


/**
 * 标准化产品单元库存
 *
 * @author qh
 * @date 2022/10/30 18:43:50
 */
@Data
public class SkuHasStockVo {

    private Long skuId;

    private Boolean hasStock;

}
