package com.qh.qhmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;


/**
 * 获取spu的规格参数信息
 *
 * @author 清欢
 * @date 2022/11/16 21:44:13
 */
@Data
@ToString
public class SpuItemAttrGroupVo {

    private String groupName;

    private List<Attr> attrs;

}
