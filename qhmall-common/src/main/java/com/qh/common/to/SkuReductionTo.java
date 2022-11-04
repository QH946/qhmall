package com.qh.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * SKU优惠、满减等信息的传输对象
 *
 * @author qh
 * @date 2022/10/30 18:43:24
 */
@Data
public class SkuReductionTo {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
