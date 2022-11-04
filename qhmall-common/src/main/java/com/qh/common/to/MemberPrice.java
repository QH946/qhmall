package com.qh.common.to;

import lombok.Data;

import java.math.BigDecimal;


/**
 * 会员价格传输对象
 *
 * @author qh
 * @date 2022/10/30 18:59:34
 */
@Data
public class MemberPrice {

    private Long id;
    private String name;
    private BigDecimal price;

}
