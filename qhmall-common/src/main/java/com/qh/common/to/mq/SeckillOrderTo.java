package com.qh.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀订单
 *
 * @author 清欢
 * @date 2022/12/11  16:11:15
 */
@Data
public class SeckillOrderTo{
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;

    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀数量
     */
    private Integer num;
    /**
     * 会员id
     */
    private Long memberId;
}
