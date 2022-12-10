package com.qh.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 库存异常
 *
 * @author 清欢
 * @date 2022/12/03  17:18:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NoStockException extends RuntimeException{
    private Long skuId;
    public NoStockException(Long skuId){
        super("商品id："+skuId+ "；没有足够的库存了");
    }

    public NoStockException(String msg){
        super(msg);
    }
}
