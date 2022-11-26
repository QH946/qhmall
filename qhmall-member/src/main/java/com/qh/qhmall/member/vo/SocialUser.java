package com.qh.qhmall.member.vo;

import lombok.Data;


/**
 * 社交用户信息
 *
 * @author 清欢
 * @date 2022/11/24 17:48:39
 */
@Data
public class SocialUser {

    private String access_token;

    private String remind_in;

    private long expires_in;

    private String uid;

    private String isRealName;

}
