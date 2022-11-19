package com.qh.qhmall.search.feign;


import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * 商品服务远程调用
 *
 * @author 清欢
 * @date 2022/11/13 15:39:39
 */
@FeignClient("qhmall-product")
public interface ProductFeignService {

    /**
     * 查询属性详细
     *
     * @param attrId attr id
     * @return {@link R}
     */
    @GetMapping("/product/attr/info/{attrId}")
    R attrInfo(@PathVariable("attrId") Long attrId);

}
