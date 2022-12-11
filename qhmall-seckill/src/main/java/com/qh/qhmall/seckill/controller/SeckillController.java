package com.qh.qhmall.seckill.controller;


import com.qh.common.utils.R;
import com.qh.qhmall.seckill.service.SeckillService;
import com.qh.qhmall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 秒杀控制器
 *
 * @author 清欢
 * @date 2022/12/11  15:52:49
 */
@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 获取当前可以参与的秒杀商品信息
     *
     * @return {@link R}
     */
    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        List<SeckillSkuRedisTo> vos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(vos);
    }

    /**
     * 查询当前商品是否参与秒杀
     *
     * @param skuId sku id
     * @return {@link R}
     */
    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId){
        SeckillSkuRedisTo to = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(to);
    }


    /**
     * 秒杀
     *
     * @param killId 杀死id
     * @param key    关键
     * @param num    全国矿工工会
     * @param model  模型
     * @return {@link String}
     */
    @GetMapping("/kill")
    public String seckill(@RequestParam("killId") String killId, @RequestParam("key") String key, @RequestParam("num") Integer num, Model model){
        //判断是否登录
        String orderSn = seckillService.kill(killId,key,num);
        model.addAttribute("orderSn",orderSn);
        return "success";
    }


}
