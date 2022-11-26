package com.qh.qhmall.member.controller;

import com.qh.common.exception.BizCodeEnum;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.R;
import com.qh.qhmall.member.entity.MemberEntity;
import com.qh.qhmall.member.exception.PhoneException;
import com.qh.qhmall.member.exception.UsernameException;
import com.qh.qhmall.member.feign.CouponFeignService;
import com.qh.qhmall.member.service.MemberService;
import com.qh.qhmall.member.vo.MemberUserLoginVo;
import com.qh.qhmall.member.vo.MemberUserRegisterVo;
import com.qh.qhmall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:38:00
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private CouponFeignService couponFeignService;

    /**
     * 注册
     *
     * @param vo 签证官
     * @return {@link R}
     */
    @PostMapping(value = "/register")
    public R register(@RequestBody MemberUserRegisterVo vo) {
        try {
            memberService.register(vo);
        } catch (PhoneException e) {
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UsernameException e) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }


    /**
     * 登录
     *
     * @param vo 签证官
     * @return {@link R}
     */
    @PostMapping(value = "/login")
    public R login(@RequestBody MemberUserLoginVo vo) {
        MemberEntity memberEntity = memberService.login(vo);
        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getCode(), BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getMsg());
        }
    }


    /**
     * oauth登录
     *
     * @param socialUser 社会用户
     * @return {@link R}
     * @throws Exception 异常
     */
    @PostMapping(value = "/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser socialUser) throws Exception {
        MemberEntity memberEntity = memberService.oauthLogin(socialUser);
        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getCode(), BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getMsg());
        }
    }

    /**
     * weixin登录
     *
     * @param accessTokenInfo 访问令牌信息
     * @return {@link R}
     */
    @PostMapping(value = "/weixin/login")
    public R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo) {
        MemberEntity memberEntity = memberService.weixinLogin(accessTokenInfo);
        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getCode(), BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getMsg());
        }
    }

    /**
     * 测试获取会员下的所有优惠卷
     *
     * @return {@link R}
     */
    @RequestMapping("/coupons")
    public R testMemberCoupons() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        R membercoupons = couponFeignService.membercoupons();
        return R.ok().put("member", memberEntity).put("coupons", membercoupons.get("coupons"));
    }

    /**
     * 获取所有会员
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
