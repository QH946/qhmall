package com.qh.qhmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;


/**
 * 获取spu的销售属性组合
 *
 * @author 清欢
 * @date 2022/11/16 21:44:30
 */
@Data
@ToString
public class SkuItemSaleAttrVo {

    private Long attrId;

    private String attrName;

    private List<AttrValueWithSkuIdVO> attrValues;

}
