package com.qh.qhmall.order.dao;

import com.qh.qhmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:42:49
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
