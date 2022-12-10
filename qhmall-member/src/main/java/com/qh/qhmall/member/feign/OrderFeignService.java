package com.qh.qhmall.member.feign;


import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("qhmall-order")
public interface OrderFeignService {
    /**
     * 分页查询订单
     *
     * @param params 参数个数
     * @return {@link R}
     */
    @PostMapping("/order/order/listWithItem")
    R listWithItem(@RequestBody Map<String, Object> params);
}
