package com.qh.qhmall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qh.qhmall.product.entity.CategoryBrandRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    /**
     * 更新分类id及名称
     *
     * @param catId 猫id
     * @param name  名字
     */
    void updateCategory(@Param("catId") Long catId, @Param("name") String name);
}
