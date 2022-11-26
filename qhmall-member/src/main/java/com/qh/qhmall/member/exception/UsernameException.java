package com.qh.qhmall.member.exception;

/**
 * 用户名异常
 *
 * @author 清欢
 * @date 2022/11/24 17:47:50
 */
public class UsernameException extends RuntimeException {
    public UsernameException() {
        super("存在相同的用户名");
    }
}
