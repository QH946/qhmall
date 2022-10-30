package com.qh.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 标准化产品单元界限
 *
 * @author qh
 * @date 2022/10/30 18:43:00
 */
@Data
public class SpuBoundTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
