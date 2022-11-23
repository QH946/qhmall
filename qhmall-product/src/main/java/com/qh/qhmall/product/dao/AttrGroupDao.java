package com.qh.qhmall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qh.qhmall.product.entity.AttrGroupEntity;
import com.qh.qhmall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    /**
     * 查询当前spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
     *
     * @param spuId     spu id
     * @param catalogId 目录id
     * @return {@link List}<{@link SpuItemAttrGroupVo}>
     */
    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
