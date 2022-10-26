package com.qh.qhmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:38:00
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

