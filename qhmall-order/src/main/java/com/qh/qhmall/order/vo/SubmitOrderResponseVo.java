package com.qh.qhmall.order.vo;


import com.qh.qhmall.order.entity.OrderEntity;
import lombok.Data;


/**
 * 提交订单响应
 *
 * @author 清欢
 * @date 2022/12/03  17:01:08
 */
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    /**
     * 错误状态码 0成功
     */
    private Integer code;
}
