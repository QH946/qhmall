package com.qh.qhmall.seckill.feign;


import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 优惠券服务
 *
 * @author 清欢
 * @date 2022/12/11  15:56:01
 */
@FeignClient("qhmall-coupon")
public interface CouponFeignService {

    /**
     * 查询每场活动关联的秒杀商品
     *
     * @return {@link R}
     */
    @GetMapping("/coupon/seckillsession/lates3DaySession")
    R getLates3DaySession();
}
