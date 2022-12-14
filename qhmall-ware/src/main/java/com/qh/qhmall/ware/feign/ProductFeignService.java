package com.qh.qhmall.ware.feign;


import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 商品服务
 *
 * @author 清欢
 * @date 2022/12/04  20:02:58
 */
@FeignClient("qhmall-product")
public interface ProductFeignService {

    /**
     *      /product/skuinfo/info/{skuId}
     * <p>
     *
     *   1)、让所有请求过网关；
     *          1、@FeignClient("qhmall-gateway")：给qhmall-gateway所在的机器发请求
     *          2、/api/product/skuinfo/info/{skuId}
     * <p>
     *   2）、直接让后台指定服务处理
     *          1、@FeignClient("qhmall-product")
     *          2、/product/skuinfo/info/{skuId}
     *
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);
}
