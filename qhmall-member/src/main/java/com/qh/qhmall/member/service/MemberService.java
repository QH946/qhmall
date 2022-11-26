package com.qh.qhmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.member.entity.MemberEntity;
import com.qh.qhmall.member.exception.PhoneException;
import com.qh.qhmall.member.exception.UsernameException;
import com.qh.qhmall.member.vo.MemberUserLoginVo;
import com.qh.qhmall.member.vo.MemberUserRegisterVo;
import com.qh.qhmall.member.vo.SocialUser;

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

    /**
     * 注册
     *
     * @param vo 签证官
     */
    void register(MemberUserRegisterVo vo);

    /**
     * 判断手机号是否重复
     *
     * @param phone 电话
     * @throws PhoneException 电话例外
     */
    void checkPhoneUnique(String phone) throws PhoneException;

    /**
     * 判断用户名是否重复
     *
     * @param userName 用户名
     * @throws UsernameException 用户名例外
     */
    void checkUserNameUnique(String userName) throws UsernameException;

    /**
     * 登录
     *
     * @param vo 签证官
     * @return {@link MemberEntity}
     */
    MemberEntity login(MemberUserLoginVo vo);

    /**
     * oauth登录
     *
     * @param socialUser 社会用户
     * @return {@link MemberEntity}
     */
    MemberEntity oauthLogin(SocialUser socialUser) throws Exception;

    /**
     * weixin登录
     *
     * @param accessTokenInfo 访问令牌信息
     * @return {@link MemberEntity}
     */
    MemberEntity weixinLogin(String accessTokenInfo);
}

