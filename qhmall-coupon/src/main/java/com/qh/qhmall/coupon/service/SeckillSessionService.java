package com.qh.qhmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.coupon.entity.SeckillSessionEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:40:37
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询每场活动关联的秒杀商品
     *
     * @return {@link List}<{@link SeckillSessionEntity}>
     */
    List<SeckillSessionEntity> getLates3DaySession();
}

