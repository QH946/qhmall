package com.qh.qhmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.product.entity.AttrEntity;
import com.qh.qhmall.product.vo.AttrGroupRelationVo;
import com.qh.qhmall.product.vo.AttrRespVo;
import com.qh.qhmall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 新增商品属性
     *
     * @param attr
     */
    void saveAttr(AttrVo attr);

    /**
     * 查询商品属性
     *
     * @param params    参数个数
     * @param catelogId catelog id
     * @return {@link PageUtils}
     */
    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    /**
     * 查询属性详细
     *
     * @param attrId attr id
     * @return {@link AttrRespVo}
     */
    AttrRespVo getAttrInfo(Long attrId);

    /**
     * 修改商品属性
     *
     * @param attr attr
     */
    void updateAttr(AttrVo attr);

    /**
     * 获取属性分组关联的属性
     *
     * @param attrgroupId attrgroup id
     * @return {@link List}<{@link AttrEntity}>
     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);

    /**
     * 删除属性与分组的关联
     *
     * @param vos vos
     */
    void deleteRelation(AttrGroupRelationVo[] vos);

    /**
     * 获取分组下没有关联的属性
     *
     * @param params      参数个数
     * @param attrgroupId attrgroup id
     * @return {@link PageUtils}
     */
    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);


    /**
     * 根据attrIds查询检索属性
     *
     * @param attrIds attr id
     * @return {@link List}<{@link Long}>
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

