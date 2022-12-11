package com.qh.qhmall.seckill.service;


import com.qh.qhmall.seckill.to.SeckillSkuRedisTo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 秒杀服务
 *
 * @author 清欢
 * @date 2022/12/11  15:54:18
 */
@Service
public interface SeckillService {
    /**
     * 上架未来三天的商品
     */
    void upSeckillSkuLatest3Days();

    /**
     * 获取当前可以参与的秒杀商品信息
     *
     * @return {@link List}<{@link SeckillSkuRedisTo}>
     */
    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    /**
     * 查询当前商品是否参与秒杀
     *
     * @param skuId sku id
     * @return {@link SeckillSkuRedisTo}
     */
    SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

    /**
     * 秒杀
     *
     * @param killId 杀死id
     * @param key    关键
     * @param num    全国矿工工会
     * @return {@link String}
     */
    String kill(String killId, String key, Integer num);
}
