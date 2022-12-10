package com.qh.qhmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.qhmall.order.dao.OrderItemDao;
import com.qh.qhmall.order.entity.OrderItemEntity;
import com.qh.qhmall.order.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.Map;


//@RabbitListener(queues = {"hello-java-queue"})  声明需要监听的所有队列
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * quques:声名要监听的队列
     * <p>
     * org.springframework.amqp.core.Message;
     * <p>
     * 参数可以写一下类型
     * 1、Message message：原生消息详细信息。头+体
     * 2、T<发送的消息的类型> OrderReturnReasonEntity content;
     * 3、Channel channel:当前传输数据的通道
     * <p>
     * Queue:可以很多人都来监听。只要收到消息，队列删除消息，而且只能有一个收到此消息
     * 场景：
     * 1）、订单服务启动多个；同一个消息,只能有一个客户端收到
     * 2）、只有一个消息完全处理完，方法运行结束，我们就可以接收到下一个消息
     */

  /*  @RabbitHandler
    public void receiveMessage(Message message,
                               OrderReturnReasonEntity content,
                               Channel channel) throws InterruptedException {
        System.out.println("接收到消息...." + content);
        byte[] body = message.getBody();
        //消息头属性信息
        MessageProperties messageProperties = message.getMessageProperties();
//        Thread.sleep(3000);
        System.out.println("消息处理完成=>" + content.getName());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("deliveryTag==>" + deliveryTag);
        //签收货物，非批量模式
        try {
            if (deliveryTag % 2 == 0) {
                channel.basicAck(deliveryTag, false);
                System.out.println("签收了货物。。。" + deliveryTag);
            } else {
                channel.basicNack(deliveryTag, false, false);
//                channel.basicReject();//同上，只是不能批量处理
                System.out.println("没有签收货物。。。" + deliveryTag);
            }
        } catch (IOException e) {
            //网络中断
        }
    }

    @RabbitHandler
    public void receiveMessage2(OrderEntity content) throws InterruptedException {
        System.out.println("消息处理完成=>" + content.getOrderSn());
    }*/

}