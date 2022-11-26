package com.qh.qhmall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;


/**
 * springSession配置类
 *
 * @author 清欢
 * @date 2022/11/24 15:00:44
 */
@Configuration
public class MallSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {

        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();

        //放大作用域
        //设置一个域名
        cookieSerializer.setDomainName("qhmall.com");
        //cookie的路径
        cookieSerializer.setCookieName("qhSESSION");

        return cookieSerializer;
    }


    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        //使用jackson提供的转换器
        return new GenericJackson2JsonRedisSerializer();
    }

}
