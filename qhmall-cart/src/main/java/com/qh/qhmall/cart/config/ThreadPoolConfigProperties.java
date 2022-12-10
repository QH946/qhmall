package com.qh.qhmall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 线程池配置属性
 *
 * @author 清欢
 * @date 2022/12/01  19:28:12
 */
@ConfigurationProperties(prefix = "qhmall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {

    private Integer coreSize;

    private Integer maxSize;

    private Integer keepAliveTime;


}
