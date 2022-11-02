package com.qh.qhmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.product.entity.CategoryBrandRelationEntity;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 新增品牌分类关联
     *
     * @param categoryBrandRelation 类别品牌关系
     */
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 更新品牌id及名称
     *
     * @param brandId 品牌标识
     * @param name    名字
     */
    void updateBrand(Long brandId, String name);

    /**
     * 更新分类id及名称
     *
     * @param catId 猫id
     * @param name  名字
     */
    void updateCategory(Long catId, String name);
}

