package com.qh.qhmall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qh.qhmall.product.entity.SpuInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    /**
     * 修改当前spu的状态
     *
     * @param spuId spu id
     * @param code  代码
     */
    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
