package com.qh.qhmall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * 合并采购
 *
 * @author qh
 * @date 2022/11/05 14:28:11
 */
@Data
public class MergeVo {

   private Long purchaseId; //整单id
   private List<Long> items;//[1,2,3,4] //合并项集合
}
