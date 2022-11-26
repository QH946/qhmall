package com.qh.qhmall.authserver.vo;

import lombok.Data;


/**
 * 用户登录
 *
 * @author 清欢
 * @date 2022/11/24 15:04:55
 */
@Data
public class UserLoginVo {

    private String loginAcct;

    private String password;
}
