package com.qh.qhmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.product.entity.AttrGroupEntity;
import com.qh.qhmall.product.vo.AttrGroupWithAttrsVo;
import com.qh.qhmall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 分页查询属性分组
     *
     * @param params    参数个数
     * @param catelogId catelog id
     * @return {@link PageUtils}
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    /**
     * 获取分类下所有分组及关联属性
     *
     * @param catelogId catelog id
     * @return {@link List}<{@link AttrGroupWithAttrsVo}>
     */
    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    /**
     * 获取spu的规格参数信息
     *
     * @param spuId     spu id
     * @param catalogId 目录id
     * @return {@link List}<{@link SpuItemAttrGroupVo}>
     */
    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

