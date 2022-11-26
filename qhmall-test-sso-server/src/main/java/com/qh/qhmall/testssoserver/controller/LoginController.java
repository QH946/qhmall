package com.qh.qhmall.testssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


/**
 * 登录控制器
 *
 * @author 清欢
 * @date 2022/11/26 15:45:58
 */
@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @ResponseBody
    @GetMapping("/userInfo")
    public String userInfo(String token){
        String s = stringRedisTemplate.opsForValue().get(token);
        return s;
    }

    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url")String url,Model model
            , @CookieValue(value = "sso_token", required = false) String sso_token){
       if (!StringUtils.isEmpty(sso_token)){
           //说明之前有人登录过
           return "redirect:"+url+"?token="+sso_token;
       }

        model.addAttribute("url",url);
        return "login";
    }


    @PostMapping(value = "/doLogin")
    public String doLogin(String username,
                          String password,
                          String url,
                          HttpServletResponse response) {
        if (!StringUtils.isEmpty(username)&&!StringUtils.isEmpty(password)){
            //登录成功调回到之前的页面
            //登录成功的用户存到redis
            String uuid = UUID.randomUUID().toString().replace("-","");
            stringRedisTemplate.opsForValue().set(uuid,username);
            Cookie sso_token = new Cookie("sso_token", uuid);
            response.addCookie(sso_token);
            return "redirect:"+url+"?token="+uuid;
        }

        //登录失败，重新回到login页面
        return "login";
    }
}
