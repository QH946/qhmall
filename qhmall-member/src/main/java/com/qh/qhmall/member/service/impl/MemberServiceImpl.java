package com.qh.qhmall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.qh.common.utils.HttpUtils;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.qhmall.member.dao.MemberDao;
import com.qh.qhmall.member.dao.MemberLevelDao;
import com.qh.qhmall.member.entity.MemberEntity;
import com.qh.qhmall.member.entity.MemberLevelEntity;
import com.qh.qhmall.member.exception.PhoneException;
import com.qh.qhmall.member.exception.UsernameException;
import com.qh.qhmall.member.service.MemberService;
import com.qh.qhmall.member.utils.HttpClientUtils;
import com.qh.qhmall.member.vo.MemberUserLoginVo;
import com.qh.qhmall.member.vo.MemberUserRegisterVo;
import com.qh.qhmall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {


    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 注册
     *
     * @param vo 签证官
     */
    @Override
    public void register(MemberUserRegisterVo vo) {
        MemberEntity memberEntity = new MemberEntity();
        //设置默认等级
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());
        //设置其它的默认信息
        //检查用户名和手机号是否唯一。感知异常，异常机制
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());
        memberEntity.setNickname(vo.getUserName());
        memberEntity.setUsername(vo.getUserName());
        //密码进行MD5加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setGender(0);
        memberEntity.setCreateTime(new Date());
        //保存数据
        this.baseMapper.insert(memberEntity);

    }

    /**
     * 判断手机号是否重复
     *
     * @param phone 电话
     * @throws PhoneException 电话例外
     */
    @Override
    public void checkPhoneUnique(String phone) throws PhoneException {
        Integer phoneCount = baseMapper.selectCount(new QueryWrapper<MemberEntity>()
                .eq("mobile", phone));
        if (phoneCount > 0) {
            throw new PhoneException();
        }
    }

    /**
     * 判断用户名是否重复
     *
     * @param userName 用户名
     * @throws UsernameException 用户名例外
     */
    @Override
    public void checkUserNameUnique(String userName) throws UsernameException {
        Integer usernameCount = baseMapper.selectCount(new QueryWrapper<MemberEntity>()
                .eq("username", userName));
        if (usernameCount > 0) {
            throw new UsernameException();
        }
    }

    /**
     * 登录
     *
     * @param vo 签证官
     * @return {@link MemberEntity}
     */
    @Override
    public MemberEntity login(MemberUserLoginVo vo) {
        String loginAcct = vo.getLoginAcct();
        String password = vo.getPassword();
        //1、去数据库查询 SELECT * FROM ums_member WHERE username = ? OR mobile = ?
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginAcct).or().eq("mobile", loginAcct));
        if (memberEntity == null) {
            //登录失败
            return null;
        } else {
            //获取到数据库里的password
            String password1 = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //进行密码匹配
            boolean matches = passwordEncoder.matches(password, password1);
            if (matches) {
                //登录成功
                return memberEntity;
            }
        }
        return null;
    }

    /**
     * oauth登录
     *
     * @param socialUser 社会用户
     * @return {@link MemberEntity}
     */
    @Override
    public MemberEntity oauthLogin(SocialUser socialUser) throws Exception {
        //具有登录和注册逻辑
        String uid = socialUser.getUid();

        //1、判断当前社交用户是否已经登录过系统
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("social_uid", uid));

        if (memberEntity != null) {
            //这个用户已经注册过
            //更新用户的访问令牌的时间和access_token
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            baseMapper.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        } else {
            //2、没有查到当前社交用户对应的记录我们就需要注册一个
            MemberEntity register = new MemberEntity();
            //3、查询当前社交用户的社交账号信息（昵称、性别等）
            Map<String, String> query = new HashMap<>();
            query.put("access_token", socialUser.getAccess_token());
            query.put("uid", socialUser.getUid());
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com",
                    "/2/users/show.json", "get", new HashMap<>(), query);

            if (response.getStatusLine().getStatusCode() == 200) {
                //查询成功
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                String name = jsonObject.getString("name");
                String gender = jsonObject.getString("gender");
                String profileImageUrl = jsonObject.getString("profile_image_url");

                register.setNickname(name);
                register.setGender("m".equals(gender) ? 1 : 0);
                register.setHeader(profileImageUrl);
                register.setCreateTime(new Date());
                register.setSocialUid(socialUser.getUid());
                register.setAccessToken(socialUser.getAccess_token());
                register.setExpiresIn(socialUser.getExpires_in());

                //把用户信息插入到数据库中
                baseMapper.insert(register);
            }
            return register;
        }
    }

    /**
     * weixin登录
     *
     * @param accessTokenInfo 访问令牌信息
     * @return {@link MemberEntity}
     */
    @Override
    public MemberEntity weixinLogin(String accessTokenInfo) {
        //从accessTokenInfo中获取出来两个值 access_token 和 oppenid
        //把accessTokenInfo字符串转换成map集合，根据map里面中的key取出相对应的value
        Gson gson = new Gson();
        HashMap accessMap = gson.fromJson(accessTokenInfo, HashMap.class);
        String accessToken = (String) accessMap.get("access_token");
        String openid = (String) accessMap.get("openid");

        //3、拿到access_token 和 oppenid，再去请求微信提供固定的API，获取到扫码人的信息
        //查询数据库当前用用户是否曾经使用过微信登录
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("social_uid", openid));

        if (memberEntity == null) {
            System.out.println("新用户注册");
            //访问微信的资源服务器，获取用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
            //发送请求
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultUserInfo==========" + resultUserInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //解析json
            HashMap userInfoMap = gson.fromJson(resultUserInfo, HashMap.class);
            //昵称
            String nickName = (String) userInfoMap.get("nickname");
            //性别
            Double sex = (Double) userInfoMap.get("sex");
            //微信头像
            String headimgurl = (String) userInfoMap.get("headimgurl");

            //把扫码人的信息添加到数据库中
            memberEntity = new MemberEntity();
            memberEntity.setNickname(nickName);
            memberEntity.setGender(sex.intValue());
            memberEntity.setHeader(headimgurl);
            memberEntity.setCreateTime(new Date());
            memberEntity.setSocialUid(openid);
            // register.setExpiresIn(socialUser.getExpires_in());
            baseMapper.insert(memberEntity);
        }
        return memberEntity;
    }

}
