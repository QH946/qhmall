package com.qh.qhmall.product.feign;


import com.qh.common.to.es.SkuEsModel;
import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * 搜索服务远程调用
 *
 * @author 清欢
 * @date 2022/11/13 15:42:49
 */
@FeignClient("qhmall-search")
public interface SearchFeignService {
    @PostMapping(value = "/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
