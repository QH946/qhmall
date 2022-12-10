package com.qh.qhmall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.qh.qhmall.order.config.AlipayTemplate;
import com.qh.qhmall.order.service.OrderService;
import com.qh.qhmall.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单支付监听器
 *
 * @author 清欢
 * @date 2022/12/08  19:23:08
 */
@RestController
public class OrderPayedListener {
    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;

    @RequestMapping("/payed/notify")
    public String handleAlipayed(PayAsyncVo vo, HttpServletRequest request) throws AlipayApiException, UnsupportedEncodingException {
        Map<String, String[]> map = request.getParameterMap();
//        for(String key : map.keySet()){
//            String value = request.getParameter(key);
//            System.out.println("参数名:"+key+"-->参数值："+value);
//        }
        //阿里验签方法
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(),
                alipayTemplate.getCharset(), alipayTemplate.getSign_type());
        if (signVerified) {
            System.out.println("签名验证成功...");
            return orderService.handlePayResult(vo);
        } else {
            System.out.println("签名验证失败...");
            return "error";
        }
    }
}
