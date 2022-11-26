package com.qh.qhmall.thirdparty.controller;


import com.qh.common.utils.R;
import com.qh.qhmall.thirdparty.component.SmsComponent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;


/**
 * 短信发送控制器
 *
 * @author 清欢
 * @date 2022/11/24 15:12:39
 */
@Controller
@RequestMapping(value = "/sms")
public class SmsSendController {

    @Resource
    private SmsComponent smsComponent;

    /**
     * 提供给别的服务进行调用
     *
     * @param phone
     * @param code
     * @return
     */
    @GetMapping(value = "/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        System.out.println("发送验证码服务被调用。。。。成功,code:"+code);
        //发送验证码(因为是免费套餐，只能发100条短信，因此这里不调用发短信的服务)
        //smsComponent.sendCode(phone,code);
        return R.ok();
    }
}
