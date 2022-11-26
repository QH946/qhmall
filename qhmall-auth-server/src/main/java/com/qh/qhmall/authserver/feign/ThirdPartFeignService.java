package com.qh.qhmall.authserver.feign;


import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 第三方平台远程调用
 *
 * @author 清欢
 * @date 2022/11/24 15:02:55
 */
@FeignClient("qhmall-third-party")
public interface ThirdPartFeignService {

    @GetMapping(value = "/sms/sendCode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);

}
