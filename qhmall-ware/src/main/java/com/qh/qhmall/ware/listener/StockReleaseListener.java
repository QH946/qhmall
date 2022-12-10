package com.qh.qhmall.ware.listener;


import com.qh.common.to.mq.OrderTo;
import com.qh.common.to.mq.StockLockedTo;
import com.qh.qhmall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 库存解锁监听器
 *
 * @author 清欢
 * @date 2022/12/07  21:46:59
 */
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {
    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        try {
            wareSkuService.unLockStock(to);
            System.out.println("收到接收解锁库存的信息");
            //不批量处理
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            System.out.println("rabbitMQ错误" + e.getMessage());
            //将消息重新回队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {

        try {
            System.out.println("订单关闭，准备解锁库存");
            wareSkuService.unLockStock(orderTo);
            //不批量处理
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            System.out.println("rabbitMQ错误" + e.getMessage());
            //将消息重新回队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}

