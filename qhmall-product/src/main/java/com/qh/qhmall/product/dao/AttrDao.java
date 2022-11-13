package com.qh.qhmall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qh.qhmall.product.entity.AttrEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    /**
     * 根据attrIds查询检索属性
     *
     * @param attrIds attr id
     * @return {@link List}<{@link Long}>
     */
    List<Long> selectSearchAttrIds(@Param("attrIds") List<Long> attrIds);
}
