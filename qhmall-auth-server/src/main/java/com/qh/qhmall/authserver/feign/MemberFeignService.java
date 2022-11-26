package com.qh.qhmall.authserver.feign;


import com.qh.common.utils.R;
import com.qh.qhmall.authserver.vo.SocialUser;
import com.qh.qhmall.authserver.vo.UserLoginVo;
import com.qh.qhmall.authserver.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 会员服务远程调用
 *
 * @author 清欢
 * @date 2022/11/24 15:02:31
 */
@FeignClient("qhmall-member")
public interface MemberFeignService {


    /**
     * 注册
     *
     * @param vo 签证官
     * @return {@link R}
     */
    @PostMapping(value = "/member/member/register")
    R register(@RequestBody UserRegisterVo vo);

    /**
     * 登录
     *
     * @param vo 签证官
     * @return {@link R}
     */
    @PostMapping(value = "/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    /**
     * oauth登录
     *
     * @param socialUser 社会用户
     * @return {@link R}
     * @throws Exception 异常
     */
    @PostMapping(value = "/member/member/oauth2/login")
    R oauthLogin(@RequestBody SocialUser socialUser) throws Exception;

    /**
     * weixin登录
     *
     * @param accessTokenInfo 访问令牌信息
     * @return {@link R}
     */
    @PostMapping(value = "/member/member/weixin/login")
    R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo);
}
