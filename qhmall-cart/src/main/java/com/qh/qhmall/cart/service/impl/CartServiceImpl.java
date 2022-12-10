package com.qh.qhmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.qh.common.utils.R;
import com.qh.qhmall.cart.feign.ProductFeignService;
import com.qh.qhmall.cart.interceptor.CartInterceptor;
import com.qh.qhmall.cart.service.CartService;
import com.qh.qhmall.cart.to.UserInfoTo;
import com.qh.qhmall.cart.vo.CartItemVo;
import com.qh.qhmall.cart.vo.CartVo;
import com.qh.qhmall.cart.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.qh.common.constant.CartConstant.CART_PREFIX;


@Slf4j
@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private ThreadPoolExecutor executor;


    /**
     * 将商品添加至购物车
     *
     * @param skuId sku id
     * @param num   全国矿工工会
     * @return {@link CartItemVo}
     * @throws ExecutionException   执行异常
     * @throws InterruptedException 中断异常
     */
    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {

        //拿到要操作的购物车信息
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        //判断redis中是否有该商品的信息
        String productRedisValue = (String) cartOps.get(skuId.toString());
        //如果没有就添加数据
        if (StringUtils.isEmpty(productRedisValue)) {

            //2、添加新的商品到购物车(redis)
            CartItemVo cartItemVo = new CartItemVo();
            //开启第一个异步任务
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                //1、远程查询当前要添加商品的信息
                R productSkuInfo = productFeignService.getSkuInfo(skuId);
                SkuInfoVo skuInfo = productSkuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                //数据赋值操作
                cartItemVo.setSkuId(skuInfo.getSkuId());
                cartItemVo.setTitle(skuInfo.getSkuTitle());
                cartItemVo.setImage(skuInfo.getSkuDefaultImg());
                cartItemVo.setPrice(skuInfo.getPrice());
                cartItemVo.setCount(num);
            }, executor);

            //开启第二个异步任务
            CompletableFuture<Void> getSkuAttrValuesFuture = CompletableFuture.runAsync(() -> {
                //2、远程查询skuAttrValues组合信息
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVo.setSkuAttr(skuSaleAttrValues);
            }, executor);

            //等待所有的异步任务全部完成
            CompletableFuture.allOf(getSkuInfoFuture, getSkuAttrValuesFuture).get();
            String cartItemJson = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(), cartItemJson);

            return cartItemVo;
        } else {
            //购物车有此商品，修改数量即可
            CartItemVo cartItemVo = JSON.parseObject(productRedisValue, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount() + num);
            //修改redis的数据
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItemVo));

            return cartItemVo;
        }
    }


    /**
     * 获取购物车中某个购物项
     *
     * @param skuId sku id
     * @return {@link CartItemVo}
     */
    @Override
    public CartItemVo getCartItem(Long skuId) {
        //拿到要操作的购物车信息
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String redisValue = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(redisValue, CartItemVo.class);
    }

    /**
     * 获取购物车里面的信息
     *
     * @return {@link CartVo}
     * @throws ExecutionException   执行异常
     * @throws InterruptedException 中断异常
     */
    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        if (userInfoTo.getUserId() != null) {
            //1登录
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            //临时购物车的键
            String temptCartKey = CART_PREFIX + userInfoTo.getUserKey();
            //2若临时购物车的数据还未进行合并，则将临时购物车合并到已登录购物车里面
            //判断临时购物车中是否有数据
            List<CartItemVo> tempCartItems = getCartItems(temptCartKey);
            if (tempCartItems != null) {
                //临时购物车有数据需要进行合并操作
                for (CartItemVo item : tempCartItems) {
                    addToCart(item.getSkuId(), item.getCount());
                }
                //清除临时购物车的数据
                clearCartInfo(temptCartKey);
            }
            //3获取登录后的购物车数据【包含合并过来的临时购物车的数据和登录后购物车的数据】
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        } else {
            //未登录
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            //获取临时购物车里面的所有购物项
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        }

        return cartVo;
    }

    /**
     * 清空购物车的数据
     *
     * @param cartKey 车钥匙
     */
    @Override
    public void clearCartInfo(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    /**
     * 勾选购物项
     *
     * @param skuId sku id
     * @param check 检查
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        //查询购物车中的商品
        CartItemVo cartItem = getCartItem(skuId);
        //修改商品状态
        cartItem.setCheck(check == 1);
        //序列化存入redis
        String redisValue = JSON.toJSONString(cartItem);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), redisValue);
    }

    /**
     * 修改购物项数量
     *
     * @param skuId sku id
     * @param num   全国矿工工会
     */
    @Override
    public void changeItemCount(Long skuId, Integer num) {
        //查询购物车里面的商品
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        //序列化存入redis
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String redisValue = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), redisValue);
    }

    /**
     * 删除购物项
     *
     * @param skuId sku id
     */
    @Override
    public void deleteIdCartInfo(Integer skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }


    /**
     * 获取当前用户购物车的商品项
     *
     * @return {@link List}<{@link CartItemVo}>
     */
    @Override
    public List<CartItemVo> getCurrentUserCartItems() {
        List<CartItemVo> cartItemVoList = new ArrayList<>();
        //获取当前用户登录的信息
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        //如果用户未登录直接返回null
        if (userInfoTo == null) {
            return null;
        } else {
            //获取购物车中的购物项
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItemVo> cartItems = getCartItems(cartKey);
            if (cartItems != null) {
                //筛选出选中的购物项
                cartItemVoList = cartItems
                        .stream()
                        .filter(CartItemVo::getCheck)
                        .peek(item -> {
                            //更新为最新的价格（查询数据库）
                            BigDecimal skuPrice =
                                    productFeignService.getSkuPrice(item.getSkuId());
                            item.setPrice(skuPrice);
                        }).collect(Collectors.toList());
            }
        }
        return cartItemVoList;
    }


    /**
     * 获取要操作的购物车
     *
     * @return {@link BoundHashOperations}<{@link String}, {@link Object}, {@link Object}>
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        //先得到当前用户信息
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            //qhmall:cart:1
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }

        //绑定指定的key操作redis
        return redisTemplate.boundHashOps(cartKey);
    }

    /**
     * 获取指定用户 (登录用户/临时用户) 购物车里面的数据
     *
     * @param cartKey 车钥匙
     * @return {@link List}<{@link CartItemVo}>
     */
    private List<CartItemVo> getCartItems(String cartKey) {
        //获取购物车里面的所有商品
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            return values.stream().map((obj) -> {
                String str = (String) obj;
                return JSON.parseObject(str, CartItemVo.class);
            }).collect(Collectors.toList());
        }
        return null;
    }

}
