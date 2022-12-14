package com.qh.qhmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.constant.ProductConstant;
import com.qh.common.to.SkuHasStockVo;
import com.qh.common.to.SkuReductionTo;
import com.qh.common.to.SpuBoundTo;
import com.qh.common.to.es.SkuEsModel;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.common.utils.R;
import com.qh.qhmall.product.dao.SpuInfoDao;
import com.qh.qhmall.product.entity.*;
import com.qh.qhmall.product.feign.CouponFeignService;
import com.qh.qhmall.product.feign.SearchFeignService;
import com.qh.qhmall.product.feign.WareFeignService;
import com.qh.qhmall.product.service.*;
import com.qh.qhmall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * ??????SPU
     *
     * @param spuSaveVo spu???????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //1?????????spu???????????? pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.baseMapper.insert(spuInfoEntity);
        //2?????????spu??????????????? pms_spu_info_desc
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        List<String> decript = spuSaveVo.getDecript();
        descEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);
        //3?????????spu???????????? pms_spu_images
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);
        //4?????????spu???????????????;pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect =
                baseAttrs.stream().map(attr -> {
                    ProductAttrValueEntity entity = new ProductAttrValueEntity();
                    entity.setSpuId(spuInfoEntity.getId());
                    entity.setAttrId(attr.getAttrId());
                    AttrEntity byId = attrService.getById(attr.getAttrId());
                    entity.setAttrName(byId.getAttrName());
                    entity.setAttrValue(attr.getAttrValues());
                    entity.setQuickShow(attr.getShowDesc());
                    return entity;
                }).collect(Collectors.toList());
        productAttrValueService.saveBatch(collect);
        //5?????????spu??????????????????qhmall_sms->sms_spu_bounds
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(spuSaveVo.getBounds(), spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        couponFeignService.saveSpuBounds(spuBoundTo);
        //5???????????????spu???????????????sku?????????
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(sku -> {
                //5.1??????sku??????????????????pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setSaleCount(0L);
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        skuInfoEntity.setSkuDefaultImg(image.getImgUrl());
                        break;
                    }
                }
                skuInfoService.save(skuInfoEntity);
                //5.2??????sku??????????????????pms_sku_image,?????????????????????????????????
                List<SkuImagesEntity> skuImagesEntities =
                        sku.getImages().stream().map(image -> {
                            SkuImagesEntity imagesEntity = new SkuImagesEntity();
                            BeanUtils.copyProperties(image, imagesEntity);
                            imagesEntity.setSkuId(skuInfoEntity.getSkuId());
                            return imagesEntity;
                        }).filter(image -> {
                            return !StringUtils.isEmpty(image.getImgUrl());
                        }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);
                //5.3??????sku????????????????????????pms_sku_sale_attr_value
                List<Attr> attr = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities =
                        attr.stream().map(at -> {
                            SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                            BeanUtils.copyProperties(at, skuSaleAttrValueEntity);
                            skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                            return skuSaleAttrValueEntity;
                        }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                // //5.4??????sku??????????????????????????????qhmall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuInfoEntity.getSkuId());
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice()
                        .compareTo(new BigDecimal("0")) == 1) {
                    R r = couponFeignService.saveSkuReduction(skuReductionTo);
                }
            });
        }
    }

    /**
     * ??????SPU????????????
     *
     * @param infoEntity ????????????
     */
    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    /**
     * ??????SPU
     *
     * @param params ????????????
     * @return {@link PageUtils}
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        // status=1 and (id=1 or spu_name like xxx)
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * ????????????
     *
     * @param spuId spu id
     */
    @Override
    public void up(Long spuId) {
        //????????????spuId???????????????sku??????,???????????????
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkuById(spuId);
        //????????????sku???????????????????????????????????????
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = baseAttrs
                .stream()
                .map(ProductAttrValueEntity::getAttrId)
                .collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        //???????????????attr_id??????set????????? ???????????? attrValueEntities???????????? attrId
        Set<Long> idSet = new HashSet<>(searchAttrIds);
        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream()
                //??????????????????searchAttrIds??????????????????
                .filter(item -> idSet.contains(item.getAttrId()))
                .map(item -> {
                    SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
                    //???ProductAttrValueEntity?????????SkuEsModel.Attrs???????????????????????????
                    BeanUtils.copyProperties(item, attrs);
                    return attrs;
                }).collect(Collectors.toList());
        //??????skuId????????????
        List<Long> skuIdList = skuInfoEntities
                .stream()
                .map(SkuInfoEntity::getSkuId)
                .collect(Collectors.toList());
        //??????????????????????????????????????????????????????
        Map<Long, Boolean> stockMap = null;
        try {
            R skuHasStock = wareFeignService.getSkuHasStock(skuIdList);
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
            };
            stockMap = skuHasStock.getData(typeReference).stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        } catch (Exception e) {
            log.error("?????????????????????????????????{}", e);
        }
        //????????????sku?????????
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> collect = skuInfoEntities.stream().map(sku -> {
            //?????????????????????
            SkuEsModel esModel = new SkuEsModel();
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            //??????????????????
            if (finalStockMap == null) {
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            //????????????????????????
            esModel.setHotScore(0L);
            //?????????????????????????????????
            BrandEntity brandEntity = brandService.getById(sku.getBrandId());
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandId(brandEntity.getBrandId());
            esModel.setBrandImg(brandEntity.getLogo());
            CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
            esModel.setCatalogId(categoryEntity.getCatId());
            esModel.setCatalogName(categoryEntity.getName());
            //??????????????????
            esModel.setAttrs(attrsList);
            BeanUtils.copyProperties(sku, esModel);
            return esModel;
        }).collect(Collectors.toList());
        //???????????????es??????????????????????????????search??????
        R r = searchFeignService.productStatusUp(collect);
        if (r.getCode() == 0) {
            //??????????????????
            //????????????spu?????????
            baseMapper.updateSpuStatus(spuId, ProductConstant.ProductStatusEnum.SPU_UP.getCode());
        } else {
            // ??????????????????
            log.error("????????????????????????{}");
            //TODO ??????????????????????????????:????????????
        }
    }

    /**
     * ???????????????spu??????
     *
     * @param skuId sku id
     * @return {@link SpuInfoEntity}
     */
    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        Long spuId = skuInfoEntity.getSpuId();
        return getById(spuId);
    }
}
