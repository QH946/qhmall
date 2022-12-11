package com.qh.qhmall.seckill.config;


import com.qh.qhmall.seckill.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 秒杀网络配置
 *
 * @author 清欢
 * @date 2022/12/11  15:52:25
 */
@Configuration
public class SeckillWebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginUserInterceptor loginUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
    }
}
