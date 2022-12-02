package com.qh.qhmall.cart.service;


import com.qh.qhmall.cart.vo.CartItemVo;
import com.qh.qhmall.cart.vo.CartVo;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * 购物车车服务
 *
 * @author 清欢
 * @date 2022/12/01  19:20:11
 */
public interface CartService {


    /**
     * 将商品添加至购物车
     *
     * @param skuId sku id
     * @param num   全国矿工工会
     * @return {@link CartItemVo}
     * @throws ExecutionException   执行异常
     * @throws InterruptedException 中断异常
     */
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;


    /**
     * 获取购物车某个购物项
     *
     * @param skuId sku id
     * @return {@link CartItemVo}
     */
    CartItemVo getCartItem(Long skuId);


    /**
     * 获取购物车里面的信息
     *
     * @return {@link CartVo}
     * @throws ExecutionException   执行异常
     * @throws InterruptedException 中断异常
     */
    CartVo getCart() throws ExecutionException, InterruptedException;


    /**
     * 清空购物车的数据
     *
     * @param cartKey 车钥匙
     */
    public void clearCartInfo(String cartKey);


    /**
     * 勾选购物项
     *
     * @param skuId sku id
     * @param check 检查
     */
    void checkItem(Long skuId, Integer check);


    /**
     * 修改购物项数量
     *
     * @param skuId sku id
     * @param num   全国矿工工会
     */
    void changeItemCount(Long skuId, Integer num);


    /**
     * 删除购物项
     *
     * @param skuId sku id
     */
    void deleteIdCartInfo(Integer skuId);


    /**
     * 获取当前用户购物车的商品项
     *
     * @return {@link List}<{@link CartItemVo}>
     */
    List<CartItemVo> getUserCartItems();

}
