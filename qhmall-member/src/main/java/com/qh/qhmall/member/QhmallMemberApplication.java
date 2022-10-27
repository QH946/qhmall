package com.qh.qhmall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages="com.qh.qhmall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class QhmallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(QhmallMemberApplication.class, args);
    }

}
