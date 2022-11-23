package com.qh.qhmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;


/**
 * 销售属性
 *
 * @author 清欢
 * @date 2022/11/22 20:44:02
 */
@Data
@ToString
public class SkuItemSaleAttrVo {

    private Long attrId;

    private String attrName;

    private List<AttrValueWithSkuIdVO> attrValues;

}
