package com.qh.qhmall.product.vo;

import lombok.Data;

import java.util.List;


/**
 * spu的规格参数信息
 *
 * @author 清欢
 * @date 2022/11/22 20:47:27
 */
@Data
public class SpuItemAttrGroupVo {

    private String groupName;

    private List<Attr> attrs;

}
