package com.qh.qhmall.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class QhmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(QhmallWareApplication.class, args);
    }

}
