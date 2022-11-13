package com.qh.qhmall.product.feign;


import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * 库存服务远程调用
 *
 * @author 清欢
 * @date 2022/11/13 14:57:53
 */
@FeignClient("qhmall-ware")
public interface WareFeignService {

    /**
     * 如何解决 发送远程调用，库存系统查询是否有库存中的返回值
     * 1.R设计时添加泛型
     * 2.直接返回想要的结果
     * 3.自己封装解析结果
     *
     * @param skuIds sku id
     * @return {@link R}
     */
    @PostMapping(value = "/ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);

}
