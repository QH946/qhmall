package com.qh.qhmall.authserver.vo;

import lombok.Data;


/**
 * 社交用户信息
 *
 * @author 清欢
 * @date 2022/11/24 15:04:43
 */
@Data
public class SocialUser {

    private String access_token;

    private String remind_in;

    private long expires_in;

    private String uid;

    private String isRealName;

}
