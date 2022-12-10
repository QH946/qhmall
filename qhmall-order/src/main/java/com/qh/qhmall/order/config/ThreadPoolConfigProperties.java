package com.qh.qhmall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 线程池配置属性
 *
 * @author 清欢
 * @date 2022/12/03  15:10:50
 */
@ConfigurationProperties(prefix = "qhmall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {

    private Integer coreSize;

    private Integer maxSize;

    private Integer keepAliveTime;

}
