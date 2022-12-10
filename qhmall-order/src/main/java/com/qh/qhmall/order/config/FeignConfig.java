package com.qh.qhmall.order.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * feign配置
 *
 * @author 清欢
 * @date 2022/12/04  18:35:34
 */
@Configuration
public class FeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return template -> {
            //RequestContextHolder拿到刚进来的请求(toTrade接口中的request)
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes!=null){
                System.out.println( "requestInterceptor线程...."+Thread.currentThread().getId());
                //老请求
                HttpServletRequest request = attributes.getRequest();
                //同步请求头数据，cookie
                //获取老请求头的cookie
                String cookie = request.getHeader("Cookie");
                //给新请求同步了老请求的cookie
                template.header("Cookie",cookie);
            }
        };
    }
}
