package com.qh.qhmall.coupon.dao;

import com.qh.qhmall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:40:37
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
