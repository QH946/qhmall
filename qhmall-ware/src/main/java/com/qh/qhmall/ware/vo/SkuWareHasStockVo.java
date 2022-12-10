package com.qh.qhmall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * 商品及仓库信息
 *
 * @author 清欢
 * @date 2022/12/05  10:24:56
 */
@Data
public class SkuWareHasStockVo {
    private Long skuId;
    private Integer num;
    private List<Long> wareId;
}
