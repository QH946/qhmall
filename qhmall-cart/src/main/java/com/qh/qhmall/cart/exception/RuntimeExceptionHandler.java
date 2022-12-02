package com.qh.qhmall.cart.exception;

import com.qh.common.utils.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 统一异常处理
 *
 * @author 清欢
 * @date 2022/12/01  19:31:27
 */
@ControllerAdvice
public class RuntimeExceptionHandler {


    /**
     * 全局统一异常处理
     *
     * @param exception 异常
     * @return {@link R}
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public R handler(RuntimeException exception) {
        return R.error(exception.getMessage());
    }


    @ExceptionHandler(CartExceptionHandler.class)
    public R userHandler(CartExceptionHandler exception) {
        return R.error("购物车无此商品");
    }
}
