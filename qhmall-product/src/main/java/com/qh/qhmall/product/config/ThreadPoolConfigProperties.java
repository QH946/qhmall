package com.qh.qhmall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 线程池配置属性
 *
 * @author 清欢
 * @date 2022/11/22 21:02:06
 */
@ConfigurationProperties(prefix = "qhmall.thread")
@Data
public class ThreadPoolConfigProperties {

    /**
     * 核心线程大小
     */
    private Integer coreSize;

    /**
     * 最大线程大小
     */
    private Integer maxSize;

    /**
     * 空闲线程的存活时间
     */
    private Integer keepAliveTime;

}
