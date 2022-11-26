package com.qh.qhmall.thirdparty.component;


import com.qh.common.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * 短信组件
 *
 * @author 清欢
 * @date 2022/11/24 15:10:50
 */
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Data
@Component
public class SmsComponent {

    private String host;
    private String path;
    private String skin;
    private String sign;
    private String appcode;

    public void sendCode(String phone, String code) {
        String method = "GET";
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> queries = new HashMap<String, String>();
        queries.put("code", code);
        queries.put("phone", phone);
        queries.put("skin", skin);
        queries.put("sign", sign);
        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, queries);
            //System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
            //状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
