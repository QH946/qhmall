package com.qh.qhmall.order.web;


import com.qh.qhmall.order.service.OrderService;
import com.qh.qhmall.order.vo.OrderConfirmVo;
import com.qh.qhmall.order.vo.OrderSubmitVo;
import com.qh.qhmall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;


/**
 * 订单web控制器
 *
 * @author 清欢
 * @date 2022/12/23  18:00:46
 */
@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    /**
     * 确认订单跳转
     *
     * @param model 模型
     * @return {@link String}
     * @throws ExecutionException   执行异常
     * @throws InterruptedException 中断异常
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }


    /**
     * 提交订单
     *
     * @param vo                 签证官
     * @param model              模型
     * @param redirectAttributes 重定向属性
     * @return {@link String}
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes) {
        //try {
        SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
        if (responseVo.getCode() == 0) {
            model.addAttribute("submitOrderResp", responseVo);
            return "pay";
        } else {
            String msg = "下单失败";
            switch (responseVo.getCode()) {
                case 1:
                    msg += "订单信息过期，请刷新重新提交";
                    break;
                case 2:
                    msg += "订单商品价格发生变化，请确认后再次提交";
                    break;
                case 3:
                    msg += "商品库存不足";
            }
            redirectAttributes.addFlashAttribute("msg", msg);
            return "redirect:http://order.qhmall.com/toTrade";
            // }
            //}catch (Exception e){
            //if(e instanceof NoStockException){
            //String msg = ((NoStockException) e).getMessage();
            //redirectAttributes.addFlashAttribute("msg",msg);
        }
        //return "redirect:http://order.qhmall.com/toTrade";
        //}
    }
}
