package com.qh.qhmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:38:01
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询会员地址
     *
     * @param memberId 成员身份
     * @return {@link List}<{@link MemberReceiveAddressEntity}>
     */
    List<MemberReceiveAddressEntity> getAddress(Long memberId);
}

