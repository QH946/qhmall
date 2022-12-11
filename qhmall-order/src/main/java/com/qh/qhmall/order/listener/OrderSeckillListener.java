package com.qh.qhmall.order.listener;


import com.qh.common.to.mq.SeckillOrderTo;
import com.qh.qhmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * 秒杀监听器
 *
 * @author 清欢
 * @date 2022/12/11  16:15:21
 */
@Slf4j
@Component
@RabbitListener(queues = "order.seckill.order.queue")
public class OrderSeckillListener {
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(SeckillOrderTo seckillOrder, Channel channel, Message msg) throws IOException {
        try {
            log.info("准备创建秒杀单的详细信息...");
            orderService.createSeckillOrder(seckillOrder);
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(msg.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
