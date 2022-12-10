package com.qh.qhmall.order.fegin;


import com.qh.common.utils.R;
import com.qh.qhmall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient("qhmall-ware")
public interface WmsFeignService {


    /**
     * 查询sku是否有库存
     *
     * @param skuIds sku id
     * @return {@link R}
     */
    @PostMapping("/ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);

    /**
     * 查询运费
     *
     * @param addrId addr id
     * @return {@link R}
     */
    @GetMapping("/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);

    /**
     * 订单锁定库存
     *
     * @param vo 签证官
     * @return {@link R}
     */
    @PostMapping("/ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo vo);

}
