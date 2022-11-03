package com.qh.qhmall.product.vo;

import lombok.Data;

/**
 * 删除属性与分组的关联时的视图对象
 *
 * @author qh
 * @date 2022/11/03 20:23:59
 */
@Data
public class AttrGroupRelationVo {

    //"attrId":1,"attrGroupId":2
    private Long attrId;
    private Long attrGroupId;
}
