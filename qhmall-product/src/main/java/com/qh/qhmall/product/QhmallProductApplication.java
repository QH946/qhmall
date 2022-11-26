package com.qh.qhmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.qh.qhmall.product.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class QhmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(QhmallProductApplication.class, args);
    }

}
