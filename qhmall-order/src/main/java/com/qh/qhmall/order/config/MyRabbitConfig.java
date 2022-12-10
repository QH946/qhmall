package com.qh.qhmall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ配置
 *
 * @author 清欢
 * @date 2022/12/09  20:00:38
 */
@Configuration
public class MyRabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "order-event-exchange");
        args.put("x-dead-letter-routing-key", "order.release.order");
        args.put("x-message-ttl", 60000);  // 订单状态改变时间要比库存早
        return new Queue("order.delay.queue",
                true,
                false,
                false,
                args);
    }

    @Bean
    public Queue orderReleaseQueue() {
        return new Queue("order.release.order.queue",
                true,
                false,
                false
        );
    }

    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange("order-event-exchange",
                true,
                false
        );
    }

    @Bean
    public Binding orderCreateOrder() {
        // 交换机根据routing-key绑定队列
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order", null);
    }

    @Bean
    public Binding orderReleaseOrder() {
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order", null);
    }

    // ware 服务
    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#", null);
    }



    @PostConstruct // 构造器MyRabbitConfig创建完成后执行该方法
    public void initRabbitTemplate() {
        // 设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 发送端确认：只要消息抵达服务器端，ack就是true
             * @param correlationData 当前消息的唯一关联数据（消息唯一id）
             * @param ack  是否确认收到
             * @param reason 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String reason) {

            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 发送端确认：只要消息没有投递给指定队列，就出发这个失败回调
             * @param message 投递失败的消息详情
             * @param i 回复消息状态码
             * @param s 回复消息文本内容
             * @param s1  to哪个交换机
             * @param s2  routing-key
             */
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
            }
        });
    }
}
