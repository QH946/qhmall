package com.qh.qhmall.product.vo;

import lombok.Data;

/**
 * 查询时，商品属性响应视图对象
 *
 * @author qh
 * @date 2022/11/03 13:43:49
 */
@Data
public class AttrRespVo extends AttrVo {
    /**
     * 			"catelogName": "手机/数码/手机", //所属分类名字
     * 			"groupName": "主体", //所属分组名字
     */
    private String catelogName;
    private String groupName;
    private Long[] catelogPath;
}
