package com.qh.qhmall.order.fegin;


import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * 商品服务
 *
 * @author 清欢
 * @date 2022/12/05  09:57:33
 */
@FeignClient("qhmall-product")
public interface ProductFeignService {
    /**
     * 获取商品的spu信息
     *
     * @param skuId sku id
     * @return {@link R}
     */
    @GetMapping("/product/spuinfo/skuId/{id}")
    R getSpuInfoBySkuId(@PathVariable("id")Long skuId);
}
