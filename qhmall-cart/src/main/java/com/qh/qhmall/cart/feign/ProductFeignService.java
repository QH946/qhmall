package com.qh.qhmall.cart.feign;


import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;


/**
 * 商品服务远程调用
 *
 * @author 清欢
 * @date 2022/12/01  19:32:00
 */
@FeignClient("qhmall-product")
public interface ProductFeignService {


    /**
     * 根据skuId查询sku信息
     *
     * @param skuId sku id
     * @return {@link R}
     */
    @GetMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);


    /**
     * 查询skuAttrValues组合信息 attr_name：attr_value
     *
     * @param skuId sku id
     * @return {@link List}<{@link String}>
     */
    @GetMapping("/product/skusaleattrvalue/stringList/{skuId}")
    List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);


    /**
     * 根据skuId查询当前商品的最新价格
     *
     * @param skuId sku id
     * @return {@link BigDecimal}
     */
    @GetMapping("/product/skuinfo/{skuId}/price")
    BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId);
}
