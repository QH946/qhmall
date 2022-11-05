package com.qh.qhmall.ware.vo;

import lombok.Data;

/**
 * 完成采购项
 *
 * @author qh
 * @date 2022/11/05 15:49:24
 */
@Data
public class PurchaseItemDoneVo {
    //{itemId:1,status:4,reason:""}
    private Long itemId;
    private Integer status;
    private String reason;
}
