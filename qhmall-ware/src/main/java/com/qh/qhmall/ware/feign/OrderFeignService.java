package com.qh.qhmall.ware.feign;


import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * 订单服务
 *
 * @author 清欢
 * @date 2022/12/04  20:02:49
 */
@FeignClient("qhmall-order")
public interface OrderFeignService {
    /**
     * 根据订单号查询订单的状态
     *
     * @param orderSn 订单sn
     * @return {@link R}
     */
    @GetMapping("/order/order/status/{orderSn}")
    R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
