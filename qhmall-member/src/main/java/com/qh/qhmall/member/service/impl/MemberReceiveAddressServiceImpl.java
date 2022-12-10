package com.qh.qhmall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.qhmall.member.dao.MemberReceiveAddressDao;
import com.qh.qhmall.member.entity.MemberReceiveAddressEntity;
import com.qh.qhmall.member.service.MemberReceiveAddressService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询会员地址
     *
     * @param memberId 成员身份
     * @return {@link List}<{@link MemberReceiveAddressEntity}>
     */
    @Override
    public List<MemberReceiveAddressEntity> getAddress(Long memberId) {
        return list(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", memberId));
    }

}