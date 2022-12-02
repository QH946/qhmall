package com.qh.qhmall.cart.to;

import lombok.Data;


/**
 * 用户信息
 *
 * @author 清欢
 * @date 2022/12/01  19:42:44
 */
@Data
public class UserInfoTo {

    private Long userId;

    private String userKey;

    /**
     * 是否为临时用户
     */
    private Boolean tempUser = false;

}
