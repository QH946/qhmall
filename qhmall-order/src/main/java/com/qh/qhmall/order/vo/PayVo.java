package com.qh.qhmall.order.vo;

import lombok.Data;

/**
 * 支付使用
 *
 * @author 清欢
 * @date 2022/12/08  16:38:37
 */
@Data
public class PayVo {
    /**
     * 商户订单号(必填)
     */
    private String out_trade_no;
    /**
     * 订单名称(必填)
     */
    private String subject;
    /**
     * 付款金额(必填)
     */
    private String total_amount;
    /**
     * 商品描述(可空)
     */
    private String body;
}
