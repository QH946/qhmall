package com.qh.qhmall.seckill.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 秒杀商品及活动关联
 *
 * @author 清欢
 * @date 2022/12/11  15:53:20
 */
@Data
public class SeckillSessionsWithSkus {

    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    private Date startTime;
    /**
     * 每日结束时间
     */
    private Date endTime;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;

    private List<SeckillSkuVo> relationSkus;
}
