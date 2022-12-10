package com.qh.qhmall.ware.feign;


import com.qh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * 会员服务
 *
 * @author 清欢
 * @date 2022/12/04  20:02:41
 */
@FeignClient("qhmall-member")
public interface MemberFeignService {

    /**
     * 获取会员地址信息
     *
     * @param id id
     * @return {@link R}
     */
    @GetMapping("/member/memberreceiveaddress/info/{id}")
    R addrInfo(@PathVariable("id") Long id);
}
