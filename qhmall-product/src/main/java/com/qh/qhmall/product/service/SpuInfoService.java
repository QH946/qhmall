package com.qh.qhmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.product.entity.SpuInfoEntity;
import com.qh.qhmall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 新增SPU
     *
     * @param vo 签证官
     */
    void saveSpuInfo(SpuSaveVo vo);

    /**
     * 保存SPU基本信息
     *
     * @param infoEntity 信息实体
     */
    void saveBaseSpuInfo(SpuInfoEntity infoEntity);

    /**
     * 查询SPU
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    PageUtils queryPageByCondition(Map<String, Object> params);
}

