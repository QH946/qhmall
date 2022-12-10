package com.qh.qhmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 地址及运费
 *
 * @author 清欢
 * @date 2022/12/03  17:02:53
 */
@Data
public class FareVo {
    /**
     * 地址
     */
    private MemberAddressVo address;
    /**
     * 运费
     */
    private BigDecimal fare;

}
