package com.qh.qhmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qh.common.utils.PageUtils;
import com.qh.qhmall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
public interface BrandService extends IService<BrandEntity> {

    /**
     * 查询品牌
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 修改品牌信息
     *
     * @param brand 品牌
     */
    void updateDetail(BrandEntity brand);
}

