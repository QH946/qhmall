package com.qh.qhmall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;


/**
 * 运费
 *
 * @author 清欢
 * @date 2022/12/04  19:50:03
 */
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;

}
