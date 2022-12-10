package com.qh.qhmall.order.fegin;


import com.qh.qhmall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@FeignClient("qhmall-cart")
public interface CartFeignService {

    /**
     * 获取当前用户购物车的商品项
     *
     * @return {@link List}<{@link OrderItemVo}>
     */
    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems();
}
