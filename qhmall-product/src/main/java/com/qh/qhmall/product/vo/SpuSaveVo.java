package com.qh.qhmall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 新增spu时的视图对象
 *
 * @author qh
 * @date 2022/11/04 15:16:10
 */
@Data
public class SpuSaveVo {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;



}