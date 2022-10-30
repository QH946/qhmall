package com.qh.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存量单位减少
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
