package com.qh.qhmall.member.exception;


/**
 * 手机号异常
 *
 * @author 清欢
 * @date 2022/11/24 17:47:40
 */
public class PhoneException extends RuntimeException {
    public PhoneException() {
        super("存在相同的手机号");
    }
}
