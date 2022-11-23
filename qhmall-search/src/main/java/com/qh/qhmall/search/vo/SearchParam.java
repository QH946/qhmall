package com.qh.qhmall.search.vo;

import lombok.Data;

import java.util.List;


/**
 * 封装页面所有可能传递过来的查询条件
 * <p>
 * catalog3Id=225&keyword=小米&sort=salecount_asc&hasStock=0/1&brandId=1&brandId=2
 * &attrs=1_5寸:8&attrs=2_16G: 8G
 *
 * @author 清欢
 * @date 2022/11/23 16:24:58
 */
@Data
public class SearchParam {

    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;

    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序条件 sort=price/salecount/hotscore_desc/asc
     */
    private String sort;

    /**
     * 是否只显示有货 0(无库存)1(有库存)
     */
    private Integer hasStock;

    /**
     * 价格区间查询
     */
    private String skuPrice;

    /**
     * 按照品牌进行查询，可以多选
     */
    private List<Long> brandId;

    /**
     * 按照属性进行筛选
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 原生的所有查询条件
     */
    private String _queryString;
}
