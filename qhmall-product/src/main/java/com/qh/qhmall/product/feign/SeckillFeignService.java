package com.qh.qhmall.product.feign;


import com.qh.common.utils.R;
import com.qh.qhmall.product.feign.fallback.SeckillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 秒杀服务
 *
 * @author 清欢
 * @date 2022/12/11  18:46:24
 */
@FeignClient(value = "qhmall-seckill" ,fallback = SeckillFeignServiceFallBack.class)
public interface SeckillFeignService {
    /**
     * 查询当前商品是否参与秒杀
     *
     * @param skuId sku id
     * @return {@link R}
     */
    @GetMapping("/sku/seckill/{skuId}")
    R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
