package com.qh.qhmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查出所有分类以及子分类，以树形结构组装起来
     *
     * @return {@link List}<{@link CategoryEntity}>
     */
    List<CategoryEntity> listWithTree();

    /**
     * 批量删除分类
     *
     * @param asList 正如列表
     */
    void removeMenuByIds(List<Long> asList);

    /**
     * 查询catelogId的完整路径；
     * [父/子/孙]
     *
     * @param catelogId catelog id
     * @return {@link Long[]}
     */
    Long[] findCatelogPath(Long catelogId);

    /**
     * 级联更新分类的所有关联的数据
     *
     * @param category 类别
     */
    void updateCascade(CategoryEntity category);
}

