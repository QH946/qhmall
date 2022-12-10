package com.qh.qhmall.order.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;


/**
 * 兔子控制器
 *
 * @author 清欢
 * @date 2022/12/03  15:57:57
 */
@Slf4j
@RestController
public class RabbitTestController {

 /*   @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMq")
    public String sendMq(@RequestParam(value = "num",defaultValue = "10") Integer num){
        for (int i = 0; i < num; i++) {
            if (i%2==0){
                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
                orderReturnReasonEntity.setId(1L);
                orderReturnReasonEntity.setCreateTime(new Date());
                orderReturnReasonEntity.setName("哈哈-"+i);
                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderReturnReasonEntity,new CorrelationData(UUID.randomUUID().toString()));
                log.info("消息发送完成{}",orderReturnReasonEntity);
            }else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange","hello22.java",orderEntity,new CorrelationData(UUID.randomUUID().toString()));
            }
        }
        return "ok";
    }*/
}
