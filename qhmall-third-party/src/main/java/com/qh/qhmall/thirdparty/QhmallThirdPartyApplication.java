package com.qh.qhmall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class QhmallThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(QhmallThirdPartyApplication.class, args);
    }

}
