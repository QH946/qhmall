package com.qh.qhmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.qhmall.product.dao.BrandDao;
import com.qh.qhmall.product.entity.BrandEntity;
import com.qh.qhmall.product.service.BrandService;
import com.qh.qhmall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 查询品牌
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //获取key
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("brand_id", key).or().like("name", key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper

        );
        return new PageUtils(page);
    }

    /**
     * 修改品牌信息
     *
     * @param brand 品牌
     */
    @Override
    public void updateDetail(BrandEntity brand) {
        //保证冗余字段的数据一致
        //修改品牌表中信息
        this.updateById(brand);
        //同步更新其他关联表中的数据
        if (!StringUtils.isEmpty(brand.getName())) {
            categoryBrandRelationService.updateBrand(brand.getBrandId(),
                    brand.getName());
        }
        //TODO 更新其他关联
    }

    /**
     * 批量获取品牌信息
     *
     * @param brandIds 品牌标识
     * @return {@link List}<{@link BrandEntity}>
     */
    @Override
    public List<BrandEntity> getBrandsByIds(List<Long> brandIds) {
        return baseMapper.selectList(new QueryWrapper<BrandEntity>().in("brand_id", brandIds));
    }
}
