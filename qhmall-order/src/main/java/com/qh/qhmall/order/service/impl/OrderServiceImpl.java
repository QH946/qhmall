package com.qh.qhmall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.exception.NoStockException;
import com.qh.common.to.mq.OrderTo;
import com.qh.common.to.mq.SeckillOrderTo;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.common.utils.R;
import com.qh.common.vo.MemberResponseVo;
import com.qh.qhmall.order.constant.OrderConstant;
import com.qh.qhmall.order.dao.OrderDao;
import com.qh.qhmall.order.entity.OrderEntity;
import com.qh.qhmall.order.entity.OrderItemEntity;
import com.qh.qhmall.order.entity.PaymentInfoEntity;
import com.qh.qhmall.order.enume.OrderStatusEnum;
import com.qh.qhmall.order.fegin.CartFeignService;
import com.qh.qhmall.order.fegin.MemberFeignService;
import com.qh.qhmall.order.fegin.ProductFeignService;
import com.qh.qhmall.order.fegin.WmsFeignService;
import com.qh.qhmall.order.interceptor.LoginUserInterceptor;
import com.qh.qhmall.order.service.OrderItemService;
import com.qh.qhmall.order.service.OrderService;
import com.qh.qhmall.order.service.PaymentInfoService;
import com.qh.qhmall.order.to.OrderCreateTo;
import com.qh.qhmall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


    ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private CartFeignService cartFeginService;
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private WmsFeignService wmsFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private PaymentInfoService paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 确认订单
     *
     * @return {@link OrderConfirmVo}
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberResponseVo memberRespVo = LoginUserInterceptor.loginUser.get();
        System.out.println("主线程...." + Thread.currentThread().getId());
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> AddressFuture = CompletableFuture.runAsync(() -> {
            //1、远程查询所有的收货地址列表
            RequestContextHolder.setRequestAttributes(attributes);
            System.out.println("member线程...." + Thread.currentThread().getId());
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
        }, executor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //2、远程查询购物车所有选中的购物项
            System.out.println("cart线程...." + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(attributes);
            List<OrderItemVo> items = cartFeginService.getCurrentUserCartItems();
            confirmVo.setItems(items);
            //feign在远程调用之前要构造请求，调用很多的拦截器
            // RequestInterceptor interceptor : requestInterceptor
        }, executor).thenRunAsync(() -> {
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> collect = items.stream()
                    .map(OrderItemVo::getSkuId)
                    .collect(Collectors.toList());
            R skusHasStock = wmsFeignService.getSkuHasStock(collect);
            List<SkuStockVo> data = skusHasStock.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null) {
                Map<Long, Boolean> map = data.stream()
                        .collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        }, executor);
        //3、查询用户积分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegeration(integration);
        //4、其它数据自动计算
        CompletableFuture.allOf(AddressFuture, cartFuture).get();
        //5、防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX +
                memberRespVo.getId(), token, 30, TimeUnit.SECONDS);
        confirmVo.setOrderToken(token);
        return confirmVo;
    }

    /**
     * 提交订单
     * <p>
     * 下单：去创建订单，验令牌，验价格，锁库存......
     * <p>
     * 本地事务，在分布式系统，只能控制住自己的回滚，控制不了其他服务的回滚
     * <p>
     * 分布式事务:最大原因。网络问题+分布式机器。
     * <p>
     * //@GlobalTransactional 高并发
     *
     * @param vo 签证官
     * @return {@link SubmitOrderResponseVo}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        confirmVoThreadLocal.set(vo);
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        MemberResponseVo memberRespVo = LoginUserInterceptor.loginUser.get();
        response.setCode(0);
        //1、验证令牌【令牌的对比和删除必须保证原子性】
        //0令牌失败-1删除成功
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        //原子验证令牌和删除令牌
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class)
                , Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId())
                , orderToken);
        if (result == 0L) {
            //令牌验证失败
            response.setCode(1);
            return response;
        } else {
            //令牌验证成功
            //下单：去创建订单，验令牌，验价格，锁库存......
            //1、创建订单.订单项等信息
            OrderCreateTo order = createOrder();
            //2、验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //保存订单
                saveOrder(order);
                //4、库存锁定。只要有异常回滚订单数据
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(locks);
                //远程锁库存
                //库存成功了，但是网络原因超时了，订单回滚，库存不滚。
                //为了保证高并发。库存服务自己回滚。可以发消息给库存服务;
                //库存服务本身也可以使用自动解锁模式
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode() == 0) {
                    //锁成功了
                    response.setOrder(order.getOrder());
                    //远程扣减积分
                    //int i=10/0; 订单回滚，库存不回滚
                    //订单创建成功发送消息给MQ
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
                    return response;
                } else {
                    //锁失败了
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                }
            } else {
                //验价失败
                response.setCode(2);
                return response;
            }

        }
    }

    /**
     * 获取订单
     *
     * @param orderSn 订单sn
     * @return {@link OrderEntity}
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    /**
     * 关闭订单
     *
     * @param entity 实体
     */
    @Override
    public void closeOrder(OrderEntity entity) {
        //查询订单最新状态
        OrderEntity orderEntity = getById(entity.getId());
        if (Objects.equals(orderEntity.getStatus(), OrderStatusEnum.CREATE_NEW.getCode())) {
            //关闭订单
            OrderEntity update = new OrderEntity();
            update.setId(entity.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            updateById(update);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity, orderTo);
            try {
                //每一条消息进行日志记录(数据库保存每一条消息的详细信息)
                //定期扫描数据库将失败的消息再发送一遍
                rabbitTemplate.convertAndSend("order-event-exchange",
                        "order.release.other", orderTo);
            } catch (Exception e) {
                //将没发送成功的消息进行重试发送
            }
        }
    }

    /**
     * 获取订单的支付信息
     *
     * @param orderSn 订单sn
     * @return {@link PayVo}
     */
    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        //根据订单
        OrderEntity order = getOrderByOrderSn(orderSn);
        //查询订单项
        List<OrderItemEntity> list =
                orderItemService.list(new QueryWrapper<OrderItemEntity>()
                        .eq("order_sn", orderSn));
        payVo.setOut_trade_no(order.getOrderSn());
        //数据库中付款金额小数有4位，但是支付宝只接受2位，所以向上取整两位数
        BigDecimal bigDecimal = order.getPayAmount().setScale(2, RoundingMode.UP);
        payVo.setTotal_amount(bigDecimal.toString());
        //获取第一个订单项
        OrderItemEntity entity = list.get(0);
        payVo.setSubject(entity.getSkuName());
        payVo.setBody(entity.getSkuAttrsVals());
        return payVo;
    }

    /**
     * 分页查询订单
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberResponseVo memberRespVo = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
                        .eq("member_id", memberRespVo.getId())
                        .orderByDesc("id")
        );
        List<OrderEntity> order_sn = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> list =
                    orderItemService.list(new QueryWrapper<OrderItemEntity>()
                            .eq("order_sn", order.getOrderSn()));
            order.setItemEntities(list);
            return order;
        }).collect(Collectors.toList());
        page.setRecords(order_sn);
        return new PageUtils(page);
    }

    /**
     * 处理支付宝异步通知返回结果
     *
     * @param vo 签证官
     * @return {@link String}
     */
    @Override
    public String handlePayResult(PayAsyncVo vo) {
        //保存交易流水
        PaymentInfoEntity infoEntity = new PaymentInfoEntity();
        infoEntity.setAlipayTradeNo(vo.getTrade_no());
        infoEntity.setOrderSn(vo.getOut_trade_no());
        infoEntity.setPaymentStatus(vo.getTrade_status());
        infoEntity.setCallbackTime(vo.getNotify_time());
        paymentInfoService.save(infoEntity);
        /**
         * 支付宝交易状态说明
         *      https://opendocs.alipay.com/open/270/105902
         */
        // TRADE_FINISHED 交易结束、不可退款
        // TRADE_SUCCESS 交易支付成功
        //修改订单状态
        if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")) {
            //支付宝回调成功后，更改订单的支付状态位已支付
            String outTradeNo = vo.getOut_trade_no();
            baseMapper.updateOrderStatus(outTradeNo, OrderStatusEnum.PAYED.getCode());
        }
        return "success";
    }

    /**
     * 创建秒杀订单
     *
     * @param seckillOrder 秒杀订单
     */
    @Override
    public void createSeckillOrder(SeckillOrderTo seckillOrder) {
        //保存订单实体
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(seckillOrder.getOrderSn());
        orderEntity.setMemberId(seckillOrder.getMemberId());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        BigDecimal multiply = seckillOrder.getSeckillPrice().multiply(new BigDecimal("" + seckillOrder.getNum()));
        orderEntity.setPayAmount(multiply);
        save(orderEntity);
        //保存订单项信息
        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setOrderSn(seckillOrder.getOrderSn());
        itemEntity.setRealAmount(multiply);
        itemEntity.setSkuQuantity(seckillOrder.getNum());
        orderItemService.save(itemEntity);
    }


    /**
     * 保存订单数据
     *
     * @param order 订单
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        save(orderEntity);
        List<OrderItemEntity> orderItems = order.getOrderItems();
        for (OrderItemEntity orderItem : orderItems) {
            orderItemService.save(orderItem);
        }
    }

    /**
     * 创建订单
     *
     * @return {@link OrderCreateTo}
     */
    private OrderCreateTo createOrder() {
        OrderCreateTo createTo = new OrderCreateTo();
        //生成订单号
        String orderSn = IdWorker.getTimeId();
        //创建订单实体
        OrderEntity orderEntity = buildOrder(orderSn);
        //创建订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);
        //计算价格,积分等
        assert itemEntities != null;
        computePrice(orderEntity, itemEntities);
        createTo.setOrderItems(itemEntities);
        createTo.setOrder(orderEntity);
        return createTo;
    }

    /**
     * 创建订单实体
     *
     * @param orderSn 订单sn
     * @return {@link OrderEntity}
     */
    private OrderEntity buildOrder(String orderSn) {
        MemberResponseVo respVo = LoginUserInterceptor.loginUser.get();
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        entity.setMemberId(respVo.getId());
        OrderSubmitVo submitVo = confirmVoThreadLocal.get();
        //获取收货地址
        R fare = wmsFeignService.getFare(submitVo.getAddrId());
        FareVo fareResp = fare.getData(new TypeReference<FareVo>() {
        });
        //设置运费信息
        entity.setFreightAmount(fareResp.getFare());
        //设置收货人信息
        entity.setReceiverCity(fareResp.getAddress().getCity());
        entity.setReceiverDetailAddress(fareResp.getAddress().getDetailAddress());
        entity.setReceiverName(fareResp.getAddress().getName());
        entity.setReceiverPhone(fareResp.getAddress().getPhone());
        entity.setReceiverPostCode(fareResp.getAddress().getPostCode());
        entity.setReceiverProvince(fareResp.getAddress().getProvince());
        entity.setReceiverRegion(fareResp.getAddress().getRegion());
        //设置订单相关的状态信息
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        entity.setAutoConfirmDay(7);
        entity.setConfirmStatus(0);
        return entity;
    }

    /**
     * 创建所有订单项
     *
     * @param orderSn 订单sn
     * @return {@link List}<{@link OrderItemEntity}>
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //获取当前用户购物车的商品项
        List<OrderItemVo> currentUserCartItems = cartFeginService.getCurrentUserCartItems();
        if (currentUserCartItems != null && currentUserCartItems.size() > 0) {
            return currentUserCartItems.stream().map(cartItem -> {
                //创建每一个订单项
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                //设置订单号
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 创建每一个订单项
     *
     * @param cartItem 车项目
     * @return {@link OrderItemEntity}
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        //1、订单号（已在buildOrderItems中实现）
        //2、商品的spu信息
        Long skuId = cartItem.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        //3、商品的sku信息
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        itemEntity.setSkuAttrsVals(StringUtils.
                collectionToDelimitedString(cartItem.getSkuAttr(), ";"));
        itemEntity.setSkuQuantity(cartItem.getCount());
        //4、优惠信息[未做]
        //5、积分信息
        itemEntity.setGiftGrowth(cartItem.getPrice().intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().intValue());
        //6、订单项的价格信息
        itemEntity.setPromotionAmount(new BigDecimal("0.0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));
        //当前订单项的实际金额 总额-各种优惠
        BigDecimal origin = itemEntity.getSkuPrice()
                .multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        BigDecimal subtract = origin.subtract(itemEntity.getPromotionAmount())
                .subtract(itemEntity.getCouponAmount()).subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(subtract);
        return itemEntity;
    }

    /**
     * 计算价格,积分等
     *
     * @param orderEntity  订单实体
     * @param itemEntities 项目实体
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        BigDecimal total = new BigDecimal("0");
        BigDecimal coupon = new BigDecimal("0");
        BigDecimal integration = new BigDecimal("0");
        BigDecimal promotion = new BigDecimal("0");
        BigDecimal gift = new BigDecimal("0.0");
        BigDecimal growth = new BigDecimal("0.0");
        //叠加每一个订单项的总额信息
        for (OrderItemEntity entity : itemEntities) {
            coupon = coupon.add(entity.getCouponAmount());
            integration = integration.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            total = total.add(entity.getRealAmount());
            gift = gift.add(new BigDecimal(entity.getGiftIntegration().toString()));
            growth = growth.add(new BigDecimal(entity.getGiftGrowth().toString()));
        }
        //订单总额
        orderEntity.setTotalAmount(total);
        //应付总额
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);
        //设置积分等信息
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setDeleteStatus(0);
    }


}