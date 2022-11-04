package com.qh.qhmall.product.feign;


import com.qh.common.to.SkuReductionTo;
import com.qh.common.to.SpuBoundTo;
import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("qhmall-coupon")
public interface CouponFeignService {


    /**
     * 保存SPU积分
     * 1、CouponFeignService.saveSpuBounds(spuBoundTo);
     * 1）、@RequestBody将这个对象转为json。
     * 2）、找到qhmall-coupon服务，给/coupon/spubounds/save发送请求。
     * 将上一步转的json放在请求体位置，发送请求；
     * 3）、对方服务收到请求。请求体里有json数据。
     * (@RequestBody SpuBoundsEntity spuBounds)；将请求体的json转为SpuBoundsEntity；
     * 只要json数据模型是兼容的。双方服务无需使用同一个to
     *
     * @param spuBoundTo spu绑定到
     * @return {@link R}
     */

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);


    /**
     * 保存SKU优惠、满减等信息
     *
     * @param skuReductionTo sku减少
     * @return {@link R}
     */
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
