package com.qh.qhmall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.qhmall.ware.dao.WareOrderTaskDao;
import com.qh.qhmall.ware.entity.WareOrderTaskEntity;
import com.qh.qhmall.ware.service.WareOrderTaskService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("wareOrderTaskService")
public class WareOrderTaskServiceImpl extends ServiceImpl<WareOrderTaskDao, WareOrderTaskEntity> implements WareOrderTaskService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskEntity> page = this.page(
                new Query<WareOrderTaskEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询最新库存的状态
     *
     * @param orderSn 订单sn
     * @return {@link WareOrderTaskEntity}
     */
    @Override
    public WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn) {
        return getOne(new QueryWrapper<WareOrderTaskEntity>().eq("order_sn", orderSn));
    }

}