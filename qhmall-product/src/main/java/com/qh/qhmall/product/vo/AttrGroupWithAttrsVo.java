package com.qh.qhmall.product.vo;


import com.qh.qhmall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * 获取分类下所有分组及关联属性时的视图对象
 *
 * @author qh
 * @date 2022/11/04 14:40:43
 */
@Data
public class AttrGroupWithAttrsVo {

    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    /**
     * 属性列表
     */
    private List<AttrEntity> attrs;
}
