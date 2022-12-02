package com.qh.qhmall.cart.controller;


import com.qh.qhmall.cart.service.CartService;
import com.qh.qhmall.cart.vo.CartItemVo;
import com.qh.qhmall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * 购物车控制器
 *
 * @author 清欢
 * @date 2022/12/01  19:30:37
 */
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 获取当前用户购物车的商品项
     *
     * @return {@link List}<{@link CartItemVo}>
     */
    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItemVo> getCurrentCartItems() {
        return cartService.getUserCartItems();
    }

    /**
     * 购物车跳转页
     * <p>
     * 去购物车页面的请求
     * 浏览器有一个cookie:user-key 标识用户的身份，一个月过期
     * 如果第一次使用jd的购物车功能，都会给一个临时的用户身份:
     * 浏览器以后保存，每次访问都会带上这个cookie；
     * <p>
     * 登录：session有
     * 没登录：按照cookie里面带来user-key来做
     * 第一次，如果没有临时用户，自动创建一个临时用户
     *
     * @param model 模型
     * @return {@link String}
     * @throws ExecutionException   执行异常
     * @throws InterruptedException 中断异常
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        //快速得到用户信息：id,user-key 使用ThreadLocal(同一个线程共享数据)
        // UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        CartVo cartVo = cartService.getCart();
        model.addAttribute("cart", cartVo);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * attributes.addFlashAttribute():将数据放在session中，可以在页面中取出，但是只能取一次
     * attributes.addAttribute():将数据放在url后面
     *
     * @param skuId      sku id
     * @param num        全国矿工工会
     * @param attributes 属性
     * @return {@link String}
     * @throws ExecutionException   执行异常
     * @throws InterruptedException 中断异常
     */
    @GetMapping("/addCartItem")
    public String addCartItem(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num,
                              RedirectAttributes attributes) throws ExecutionException, InterruptedException {

        cartService.addToCart(skuId, num);
        //将数据拼接到url后面
        attributes.addAttribute("skuId", skuId);
        //重定向到对应的地址
        return "redirect:http://cart.qhmall.com/addToCartSuccessPage.html";
    }

    /**
     * 跳转到添加购物车成功页面
     *
     * @param skuId sku id
     * @param model 模型
     * @return {@link String}
     */
    @GetMapping("/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,
                                       Model model) {
        //重定向到成功页面，再次查询购物车数据
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItemVo);
        return "success";
    }


    /**
     * 商品是否选中
     *
     * @param skuId   sku id
     * @param checked 检查
     * @return {@link String}
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "checked") Integer checked) {

        cartService.checkItem(skuId, checked);

        return "redirect:http://cart.qhmall.com/cart.html";

    }


    /**
     * 修改购物项数量
     *
     * @param skuId sku id
     * @param num   全国矿工工会
     * @return {@link String}
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "num") Integer num) {

        cartService.changeItemCount(skuId, num);

        return "redirect:http://cart.qhmall.com/cart.html";
    }


    /**
     * 删除购物项
     *
     * @param skuId sku id
     * @return {@link String}
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Integer skuId) {

        cartService.deleteIdCartInfo(skuId);

        return "redirect:http://cart.qhmall.com/cart.html";

    }

}
