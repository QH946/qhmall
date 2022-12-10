package com.qh.qhmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.exception.NoStockException;
import com.qh.common.to.SkuHasStockVo;
import com.qh.common.to.mq.OrderTo;
import com.qh.common.to.mq.StockDetailTo;
import com.qh.common.to.mq.StockLockedTo;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.common.utils.R;
import com.qh.qhmall.ware.dao.WareSkuDao;
import com.qh.qhmall.ware.entity.WareOrderTaskDetailEntity;
import com.qh.qhmall.ware.entity.WareOrderTaskEntity;
import com.qh.qhmall.ware.entity.WareSkuEntity;
import com.qh.qhmall.ware.feign.OrderFeignService;
import com.qh.qhmall.ware.feign.ProductFeignService;
import com.qh.qhmall.ware.service.WareOrderTaskDetailService;
import com.qh.qhmall.ware.service.WareOrderTaskService;
import com.qh.qhmall.ware.service.WareSkuService;
import com.qh.qhmall.ware.vo.OrderItemVo;
import com.qh.qhmall.ware.vo.OrderVo;
import com.qh.qhmall.ware.vo.SkuWareHasStockVo;
import com.qh.qhmall.ware.vo.WareSkuLockVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private WareOrderTaskService orderTaskService;
    @Autowired
    private WareOrderTaskDetailService orderTaskDetailService;
    @Autowired
    private OrderFeignService orderFeignService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 查询库存
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        // 取出请求的参数 组装条件进行查询
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    /**
     * 将成功采购的进行入库
     *
     * @param skuId  sku id
     * @param wareId 器皿id
     * @param skuNum sku num
     */
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //先根据 skuId 和 ware_id 查询是否拥有这个用户
        List<WareSkuEntity> wareSkuEntities =
                wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>()
                        .eq("sku_id", skuId)
                        .eq("ware_id", wareId));
        //没有这个用户就新增
        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            //根据属性值设置
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            //TODO 除了手动try/catch 还可以用什么办法让异常出现以后不回滚
            try {
                // 远程调用 根据 skuid进行查询
                R info = productFeignService.info(skuId);
                Map<String, Object> map = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    wareSkuEntity.setSkuName((String) map.get("skuName"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            wareSkuDao.insert(wareSkuEntity);
        } else {
            //有该记录就进行更新
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    /**
     * 查询SKU是否有库存
     *
     * @param skuIds sku id
     * @return {@link List}<{@link SkuHasStockVo}>
     */
    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(item -> {
            Long count = baseMapper.getSkuStock(item);
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setSkuId(item);
            skuHasStockVo.setHasStock(count != null && count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
    }

    /**
     * 订单锁定库存
     *
     * @param vo 签证官
     * @return {@link Boolean}
     */
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        //为方便追溯，保存库存工作单的详情
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        orderTaskService.save(taskEntity);
        //1、查询每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStockVo> collect = locks.stream().map(item -> {
            SkuWareHasStockVo stock = new SkuWareHasStockVo();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询哪些仓库有库存
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());
        //2、锁定库存
        for (SkuWareHasStockVo hasStock : collect) {
            Boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                //没有任何仓库有这个商品
                throw new NoStockException(skuId);
            }
            //如果每一个商品都锁定成功，将当前商品锁定了几件的工作单记录发给MQ
            //锁定失败，前面保存的工作单信息就回滚了。发送出去的消息,即使要解锁记录，由于去数据库查不到id,就不用解锁
            for (Long wareId : wareIds) {
                //成功就返回1,否则就是0
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count == 1) {
                    skuStocked = true;
                    //告诉MQ库存锁定成功
                    WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity(null,
                            skuId, "", hasStock.getNum(), taskEntity.getId(), wareId, 1);
                    orderTaskDetailService.save(entity);
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(taskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    //防止回滚之后找不到数据,所以保存完整库存单
                    BeanUtils.copyProperties(entity, stockDetailTo);
                    lockedTo.setDetailTo(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange",
                            "stock.locked", lockedTo);
                    break;
                }
                //当前仓库锁失败(库存不足),重试下一个仓库
            }
            if (!skuStocked) {
                //当前商品所有仓库都无货(没锁住库存)
                throw new NoStockException(skuId);
            }
        }
        return true;
    }

    /**
     * 解锁库存
     *
     * @param to 来
     */
    @Override
    public void unLockStock(StockLockedTo to) {
        StockDetailTo detail = to.getDetailTo();
        Long detailId = detail.getId();
        //1、查询数据厍关于这个订单的锁定库存信息.。
        //有: 证明库存锁定成功了
        //  解锁:订单情况。
        //      1、没有这个订单。必须解锁
        //      2、有这个订单。不是解锁库存。
        //          订单状态:已取消:解锁库存
        //                  没取消:不能解锁
        //没有:库存锁定失败了,库存回滚了。这种情况无需解锁
        WareOrderTaskDetailEntity byId = orderTaskDetailService.getById(detailId);
        if (byId != null) {
            //解锁
            Long id = to.getId();
            WareOrderTaskEntity taskEntity = orderTaskService.getById(id);
            //根据订单号查询订单的状态
            String orderSn = taskEntity.getOrderSn();
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                OrderVo data = r.getData(new TypeReference<OrderVo>() {
                });
                if (data == null || data.getStatus() == 4) {

                    //订单不存在/已经被取消，才能解锁厍存且处于已锁定 1 状态的才能解锁
                    if (byId.getLockStatus() == 1) {
                        unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                }
            } else {
                //消息拒绝以后重新放到队列里面，让别人继续消费解锁。
                throw new RuntimeException("远程服务失败");
            }
        }
        // 库存锁定失败，库存回滚，这种情况无需解锁
    }


    /**
     * 解锁订单
     * <p>
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存消息优先到期。查订单状态新建状态，什么都不做就走了
     * <p>
     * 防止因为订单服务故障,导致订单状态未改变，从而无法解锁库存
     *
     * @param orderTo 以
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unLockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        //查一下最新库存的状态，防止重复解锁库存
        WareOrderTaskEntity task = orderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = task.getId();
        //按照工作单找到所有没有解锁的库存，进行解锁
        List<WareOrderTaskDetailEntity> entities =
                orderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                        .eq("task_id", id)
                        .eq("lock_status", 1));
        for (WareOrderTaskDetailEntity entity : entities) {
            unLockStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum(), entity.getId());
        }
    }

    /**
     * 解锁库存并更新工作单
     * <p>
     * 1、库存自动解锁。
     * 下订单成功,库存锁定成功，接下来的业务调用失败，导致订单回滚。之前锁定的库存就要自动解锁。
     * <p>
     * 2、订单失败/锁库存失败
     * <p>
     * 只要解锁库存的消息失败。一定要告诉服务解锁失败。
     *
     * @param skuId        sku id
     * @param wareId       器皿id
     * @param num          全国矿工工会
     * @param taskDetailId 任务详细id
     */
    private void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        //库存解锁
        wareSkuDao.unLockStock(skuId, wareId, num);
        //更新库存工作单的状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(taskDetailId);
        entity.setLockStatus(2);
        orderTaskDetailService.updateById(entity);
    }

}