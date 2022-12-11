package com.qh.qhmall.ware.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * RabbitMQ配置
 *
 * @author 清欢
 * @date 2022/12/07  20:05:52
 */
@Configuration
public class MyRabbitConfig {
    /**
     * 使用JSON序列化机制,进行消息转换
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 库存事件交换
     *
     * @return {@link Exchange}
     */
    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange("stock-event-exchange", true, false);
    }

    /**
     * 库存释放队列
     *
     * @return {@link Queue}
     */
    @Bean
    public Queue stockReleaseStockQueue() {
        return new Queue("stock.release.stock.queue", true, false, false);
    }

    /**
     * 库存延时队列
     *
     * @return {@link Queue}
     */
    @Bean
    public Queue stockDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");
        arguments.put("x-dead-letter-routing-key", "stock.release");
        arguments.put("x-message-ttl", 120000);
        return new Queue("stock.delay.queue", true, false, false, arguments);
    }

    /**
     * 库存释放绑定
     *
     * @return {@link Binding}
     */
    @Bean
    public Binding stockLockedBinding() {
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE, "stock-event-exchange", "stock.release.#", null);
    }

    /**
     * 延时队列绑定
     *
     * @return {@link Binding}
     */
    @Bean
    public Binding stockReleaseBinding() {
        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE, "stock-event-exchange", "stock.locked", null);
    }

    //第一次监听消息时，idea会连接rabbitMQ,此时才会创建rdbbitMQ中没有的队列、交换机和绑定关系
    //如果需要修改rabbitMQ中已存在的队列交换机,需要先删除，然后再次创建
//    @RabbitListener(queues = "stock.release.stock.queue")
//    public void listener(WareInfoEntity entity, Channel channel, Message msg) throws IOException {
//        System.out.println("收到过期的订单信息:准备关闭订单" + entity.getId());
//        channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
//    }
}
