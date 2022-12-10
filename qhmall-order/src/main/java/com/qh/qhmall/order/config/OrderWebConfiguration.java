package com.qh.qhmall.order.config;


import com.qh.qhmall.order.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 订单web配置
 *
 * @author 清欢
 * @date 2022/12/03  15:17:21
 */
@Configuration
public class OrderWebConfiguration implements WebMvcConfigurer {
    @Autowired
    private LoginUserInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**");
    }
}
