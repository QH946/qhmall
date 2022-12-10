package com.qh.qhmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.order.entity.OrderEntity;
import com.qh.qhmall.order.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:42:49
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 确认订单
     *
     * @return {@link OrderConfirmVo}
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 提交订单
     *
     * @param vo 签证官
     * @return {@link SubmitOrderResponseVo}
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    /**
     * 获取订单
     *
     * @param orderSn 订单sn
     * @return {@link OrderEntity}
     */
    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 关闭订单
     *
     * @param entity 实体
     */
    void closeOrder(OrderEntity entity);

    /**
     * 获取订单的支付信息
     *
     * @param orderSn 订单sn
     * @return {@link PayVo}
     */
    PayVo getOrderPay(String orderSn);

    /**
     * 分页查询订单
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    PageUtils queryPageWithItem(Map<String, Object> params);

    /**
     * 处理支付宝异步通知返回结果
     *
     * @param vo 签证官
     * @return {@link String}
     */
    String handlePayResult(PayAsyncVo vo);
}

