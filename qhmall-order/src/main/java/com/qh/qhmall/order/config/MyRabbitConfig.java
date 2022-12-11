package com.qh.qhmall.order.config;

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
 * @date 2022/12/09  20:00:38
 */
@Configuration
public class MyRabbitConfig {

//    @Autowired
//    private RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 订单延时队列
     *
     * @return {@link Queue}
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 60000);
        return new Queue("order.delay.queue", true,
                false, false, arguments);
    }

    /**
     * 订单释放队列
     *
     * @return {@link Queue}
     */
    @Bean
    public Queue orderReleaseQueue() {
        return new Queue("order.release.order.queue", true,
                false, false);
    }

    /**
     * 订单秒杀队列
     *
     * @return {@link Queue}
     */
    @Bean
    public Queue orderSeckillOrderQueue() {
        return new Queue("order.seckill.order.queue", true,
                false, false);
    }

    /**
     * 订单事件交换
     *
     * @return {@link Exchange}
     */
    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange("order-event-exchange",
                true, false);
    }

    /**
     * 订单创建绑定
     *
     * @return {@link Binding}
     */
    @Bean
    public Binding orderCreateBinding() {
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.create.order", null);
    }

    /**
     * 订单释放绑定
     *
     * @return {@link Binding}
     */
    @Bean
    public Binding orderReleaseBinding() {
        return new Binding("order.release.order.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.release.order", null);
    }

    /**
     * 库存释放绑定
     *
     * @return {@link Binding}
     */
    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.release.other.#", null);
    }

    /**
     * 订单秒杀绑定
     *
     * @return {@link Binding}
     */
    @Bean

    public Binding orderSeckillOrderQueueBinding() {
        return new Binding("order.seckill.order.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.seckill.order", null);
    }


   /* @PostConstruct // 构造器MyRabbitConfig创建完成后执行该方法
    public void initRabbitTemplate() {
        // 设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            *//**
             * 发送端确认：只要消息抵达服务器端，ack就是true
             * @param correlationData 当前消息的唯一关联数据（消息唯一id）
             * @param ack  是否确认收到
             * @param reason 失败原因
             *//*
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String reason) {

            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            *//**
             * 发送端确认：只要消息没有投递给指定队列，就出发这个失败回调
             * @param message 投递失败的消息详情
             * @param i 回复消息状态码
             * @param s 回复消息文本内容
             * @param s1  to哪个交换机
             * @param s2  routing-key
             *//*
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
            }
        });
    }*/
}
