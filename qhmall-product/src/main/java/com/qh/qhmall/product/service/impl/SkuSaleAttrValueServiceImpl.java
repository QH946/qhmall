package com.qh.qhmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.qhmall.product.dao.SkuSaleAttrValueDao;
import com.qh.qhmall.product.entity.SkuSaleAttrValueEntity;
import com.qh.qhmall.product.service.SkuSaleAttrValueService;
import com.qh.qhmall.product.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取spu的销售属性组合
     *
     * @param spuId spu id
     * @return {@link List}<{@link SkuItemSaleAttrVo}>
     */
    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId) {
        SkuSaleAttrValueDao baseMapper = this.getBaseMapper();
        return baseMapper.getSaleAttrBySpuId(spuId);
    }

}