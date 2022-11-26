package com.qh.qhmall.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.qh.qhmall.authserver.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class QhmallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(QhmallAuthServerApplication.class, args);
    }

}
