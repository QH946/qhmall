package com.qh.qhmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class QhmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(QhmallProductApplication.class, args);
    }

}
