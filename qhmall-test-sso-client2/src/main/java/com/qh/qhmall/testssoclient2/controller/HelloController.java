package com.qh.qhmall.testssoclient2.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;


/**
 * 你好控制器
 *
 * @author 清欢
 * @date 2022/11/26 15:44:54
 */
@Controller
public class HelloController {


    @Value("${sso.server.url}")
    String ssoServerUrl;

    /**
     * 无需登录就可访问
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/hello")
    public String hello() {
        return "hello";
    }

    /**
     * 需要感知是登录过后，重定向回来的
     *
     * @param model
     * @param session
     * @param token   只要去sso登录过，跳回来就会带上token
     * @return
     */
    @GetMapping(value = "/boss")
    public String employees(Model model, HttpSession session, String token) {

        if (token != null) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://ssoserver.com:8080/userInfo?token=" + token, String.class);
            String body = forEntity.getBody();
            session.setAttribute("loginUser",body);
        }

        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            //没登录，跳转到服务器进行登录
            return "redirect:" + ssoServerUrl + "?redirect_url=http://client2.com:8082/boss";
        }

        ArrayList<Object> emps = new ArrayList<>();
        emps.add("张三");
        emps.add("李四");
        model.addAttribute("emps", emps);
        return "list";
    }


}
