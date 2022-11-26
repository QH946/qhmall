package com.qh.qhmall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.qh.common.utils.HttpUtils;
import com.qh.qhmall.thirdparty.component.SmsComponent;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class MallThirdPartyApplicationTests {

    @Resource
    private OSSClient ossClient;
    @Resource
    private SmsComponent smsComponent;

    @Test
    void contextLoads() throws FileNotFoundException {
        ossClient.putObject("qhmall-hello", "hh.png", new FileInputStream("C:\\Users\\qh\\Pictures\\test.png"));
    }

    @Test
    public void sendSmsCode() {
        smsComponent.sendCode("13838383838", "134531");
    }


    @Test
    public void testUpload() throws FileNotFoundException {
//        // Endpoint以北京为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-beijing.aliyuncs.com";
//        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//        String accessKeyId = "LTAI5t5ZBiEYocB7qkRRRvkd";
//        String accessKeySecret = "CB5M5aJdkp6dQYYfVBM41kx1nH5EQy";
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//        // 上传文件流。
        InputStream inputStream = new FileInputStream("D:\\05_Doc\\Mall_Resources\\28f296629cca865e.jpg");
        ossClient.putObject("qhmall-aurora", "28f296629cca865e.jpg", inputStream);
        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传成功...");
    }

    @Test
    public void sendSms() {
        String host = "https://smsmsgs.market.alicloudapi.com";
        String path = "/sms/";
        String method = "GET";
        String appcode = "";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> queries = new HashMap<String, String>();
        queries.put("code", "12345678");
        queries.put("phone", "13838383838");
        queries.put("skin", "1");
        queries.put("sign", "175622");
        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, queries);
            System.out.println(response.toString()); //如不输出json, 请打开这行代码，打印调试头部状态码。
            //状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
