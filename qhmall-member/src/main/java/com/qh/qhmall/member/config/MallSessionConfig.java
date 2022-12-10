package com.qh.qhmall.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;


/**
 * 商城会话配置
 *
 * @author 清欢
 * @date 2022/12/03  15:16:14
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
