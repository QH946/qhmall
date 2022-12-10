package com.qh.common.to.mq;

import lombok.Data;


/**
 * 锁定库存
 *
 * @author 清欢
 * @date 2022/12/03  17:20:18
 */
@Data
public class StockLockedTo {

    /**
     * 库存工作单id
     */
    private Long id;
    /**
     * 工作单详情
     */
    private StockDetailTo detailTo;
}
