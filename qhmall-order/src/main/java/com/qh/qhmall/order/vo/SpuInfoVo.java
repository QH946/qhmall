package com.qh.qhmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * spu信息
 *
 * @author 清欢
 * @date 2022/12/03  17:02:37
 */
@Data
public class SpuInfoVo {

    /**
     * 商品id
     */
    private Long id;
    /**
     * 商品名称
     */
    private String spuName;
    /**
     * 商品描述
     */
    private String spuDescription;
    /**
     * 所属分类id
     */
    private Long catalogId;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     *
     */
    private BigDecimal weight;
    /**
     * 上架状态[0 - 下架，1 - 上架]
     */
    private Integer publishStatus;
    /**
     *
     */
    private Date createTime;
    /**
     *
     */
    private Date updateTime;

}
