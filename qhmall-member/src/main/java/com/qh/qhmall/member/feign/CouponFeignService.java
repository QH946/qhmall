package com.qh.qhmall.member.feign;

import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 声明式远程调用
 *
 * @author qh
 * @date 2022/10/27 15:45:09
 */
@FeignClient("qhmall-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();
}
