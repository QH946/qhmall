package com.qh.qhmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.qhmall.product.dao.SkuImagesDao;
import com.qh.qhmall.product.entity.SkuImagesEntity;
import com.qh.qhmall.product.service.SkuImagesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据skuId获取图片信息
     *
     * @param skuId sku id
     * @return {@link List}<{@link SkuImagesEntity}>
     */
    @Override
    public List<SkuImagesEntity> getImagesBySkuId(Long skuId) {
        return baseMapper.selectList(new
                QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
    }

}