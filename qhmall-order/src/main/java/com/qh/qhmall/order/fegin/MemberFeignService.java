package com.qh.qhmall.order.fegin;


import com.qh.qhmall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient("qhmall-member")
public interface MemberFeignService {

    /**
     * 查询会员地址
     *
     * @param memberId 成员身份
     * @return {@link List}<{@link MemberAddressVo}>
     */
    @GetMapping("/member/memberreceiveaddress/{memberId}/addresses")
    List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);
}
