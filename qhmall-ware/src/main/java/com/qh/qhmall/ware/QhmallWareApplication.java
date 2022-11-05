package com.qh.qhmall.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableFeignClients(basePackages = "com.qh.qhmall.ware.feign")
@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
public class QhmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(QhmallWareApplication.class, args);
    }

}
