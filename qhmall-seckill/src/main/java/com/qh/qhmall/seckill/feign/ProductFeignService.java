package com.qh.qhmall.seckill.feign;


import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 商品服务
 *
 * @author 清欢
 * @date 2022/12/11  15:56:13
 */
@FeignClient("qhmall-product")
public interface ProductFeignService {

    /**
     * 获取sku信息
     *
     * @param skuId sku id
     * @return {@link R}
     */
    @GetMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);
}
