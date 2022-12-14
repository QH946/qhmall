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
     * ????????????
     *
     * @return {@link OrderConfirmVo}
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberResponseVo memberRespVo = LoginUserInterceptor.loginUser.get();
        System.out.println("?????????...." + Thread.currentThread().getId());
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> AddressFuture = CompletableFuture.runAsync(() -> {
            //1??????????????????????????????????????????
            RequestContextHolder.setRequestAttributes(attributes);
            System.out.println("member??????...." + Thread.currentThread().getId());
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
        }, executor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //2????????????????????????????????????????????????
            System.out.println("cart??????...." + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(attributes);
            List<OrderItemVo> items = cartFeginService.getCurrentUserCartItems();
            confirmVo.setItems(items);
            //feign???????????????????????????????????????????????????????????????
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
        //3?????????????????????
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegeration(integration);
        //4???????????????????????????
        CompletableFuture.allOf(AddressFuture, cartFuture).get();
        //5???????????????
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX +
                memberRespVo.getId(), token, 30, TimeUnit.SECONDS);
        confirmVo.setOrderToken(token);
        return confirmVo;
    }

    /**
     * ????????????
     * <p>
     * ????????????????????????????????????????????????????????????......
     * <p>
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????
     * <p>
     * ???????????????:???????????????????????????+??????????????????
     * <p>
     * //@GlobalTransactional ?????????
     *
     * @param vo ?????????
     * @return {@link SubmitOrderResponseVo}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        confirmVoThreadLocal.set(vo);
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        MemberResponseVo memberRespVo = LoginUserInterceptor.loginUser.get();
        response.setCode(0);
        //1??????????????????????????????????????????????????????????????????
        //0????????????-1????????????
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        //?????????????????????????????????
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class)
                , Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId())
                , orderToken);
        if (result == 0L) {
            //??????????????????
            response.setCode(1);
            return response;
        } else {
            //??????????????????
            //????????????????????????????????????????????????????????????......
            //1???????????????.??????????????????
            OrderCreateTo order = createOrder();
            //2?????????
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //????????????
                saveOrder(order);
                //4???????????????????????????????????????????????????
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
                //???????????????
                //??????????????????????????????????????????????????????????????????????????????
                //?????????????????????????????????????????????????????????????????????????????????;
                //???????????????????????????????????????????????????
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode() == 0) {
                    //????????????
                    response.setOrder(order.getOrder());
                    //??????????????????
                    //int i=10/0; ??????????????????????????????
                    //?????????????????????????????????MQ
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
                    return response;
                } else {
                    //????????????
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                }
            } else {
                //????????????
                response.setCode(2);
                return response;
            }

        }
    }

    /**
     * ????????????
     *
     * @param orderSn ??????sn
     * @return {@link OrderEntity}
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    /**
     * ????????????
     *
     * @param entity ??????
     */
    @Override
    public void closeOrder(OrderEntity entity) {
        //????????????????????????
        OrderEntity orderEntity = getById(entity.getId());
        if (Objects.equals(orderEntity.getStatus(), OrderStatusEnum.CREATE_NEW.getCode())) {
            //????????????
            OrderEntity update = new OrderEntity();
            update.setId(entity.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            updateById(update);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity, orderTo);
            try {
                //?????????????????????????????????(?????????????????????????????????????????????)
                //??????????????????????????????????????????????????????
                rabbitTemplate.convertAndSend("order-event-exchange",
                        "order.release.other", orderTo);
            } catch (Exception e) {
                //?????????????????????????????????????????????
            }
        }
    }

    /**
     * ???????????????????????????
     *
     * @param orderSn ??????sn
     * @return {@link PayVo}
     */
    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        //????????????
        OrderEntity order = getOrderByOrderSn(orderSn);
        //???????????????
        List<OrderItemEntity> list =
                orderItemService.list(new QueryWrapper<OrderItemEntity>()
                        .eq("order_sn", orderSn));
        payVo.setOut_trade_no(order.getOrderSn());
        //?????????????????????????????????4??????????????????????????????2?????????????????????????????????
        BigDecimal bigDecimal = order.getPayAmount().setScale(2, RoundingMode.UP);
        payVo.setTotal_amount(bigDecimal.toString());
        //????????????????????????
        OrderItemEntity entity = list.get(0);
        payVo.setSubject(entity.getSkuName());
        payVo.setBody(entity.getSkuAttrsVals());
        return payVo;
    }

    /**
     * ??????????????????
     *
     * @param params ????????????
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
     * ???????????????????????????????????????
     *
     * @param vo ?????????
     * @return {@link String}
     */
    @Override
    public String handlePayResult(PayAsyncVo vo) {
        //??????????????????
        PaymentInfoEntity infoEntity = new PaymentInfoEntity();
        infoEntity.setAlipayTradeNo(vo.getTrade_no());
        infoEntity.setOrderSn(vo.getOut_trade_no());
        infoEntity.setPaymentStatus(vo.getTrade_status());
        infoEntity.setCallbackTime(vo.getNotify_time());
        paymentInfoService.save(infoEntity);
        /**
         * ???????????????????????????
         *      https://opendocs.alipay.com/open/270/105902
         */
        // TRADE_FINISHED ???????????????????????????
        // TRADE_SUCCESS ??????????????????
        //??????????????????
        if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")) {
            //??????????????????????????????????????????????????????????????????
            String outTradeNo = vo.getOut_trade_no();
            baseMapper.updateOrderStatus(outTradeNo, OrderStatusEnum.PAYED.getCode());
        }
        return "success";
    }

    /**
     * ??????????????????
     *
     * @param seckillOrder ????????????
     */
    @Override
    public void createSeckillOrder(SeckillOrderTo seckillOrder) {
        //??????????????????
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(seckillOrder.getOrderSn());
        orderEntity.setMemberId(seckillOrder.getMemberId());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        BigDecimal multiply = seckillOrder.getSeckillPrice().multiply(new BigDecimal("" + seckillOrder.getNum()));
        orderEntity.setPayAmount(multiply);
        save(orderEntity);
        //?????????????????????
        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setOrderSn(seckillOrder.getOrderSn());
        itemEntity.setRealAmount(multiply);
        itemEntity.setSkuQuantity(seckillOrder.getNum());
        orderItemService.save(itemEntity);
    }


    /**
     * ??????????????????
     *
     * @param order ??????
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
     * ????????????
     *
     * @return {@link OrderCreateTo}
     */
    private OrderCreateTo createOrder() {
        OrderCreateTo createTo = new OrderCreateTo();
        //???????????????
        String orderSn = IdWorker.getTimeId();
        //??????????????????
        OrderEntity orderEntity = buildOrder(orderSn);
        //???????????????
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);
        //????????????,?????????
        assert itemEntities != null;
        computePrice(orderEntity, itemEntities);
        createTo.setOrderItems(itemEntities);
        createTo.setOrder(orderEntity);
        return createTo;
    }

    /**
     * ??????????????????
     *
     * @param orderSn ??????sn
     * @return {@link OrderEntity}
     */
    private OrderEntity buildOrder(String orderSn) {
        MemberResponseVo respVo = LoginUserInterceptor.loginUser.get();
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        entity.setMemberId(respVo.getId());
        OrderSubmitVo submitVo = confirmVoThreadLocal.get();
        //??????????????????
        R fare = wmsFeignService.getFare(submitVo.getAddrId());
        FareVo fareResp = fare.getData(new TypeReference<FareVo>() {
        });
        //??????????????????
        entity.setFreightAmount(fareResp.getFare());
        //?????????????????????
        entity.setReceiverCity(fareResp.getAddress().getCity());
        entity.setReceiverDetailAddress(fareResp.getAddress().getDetailAddress());
        entity.setReceiverName(fareResp.getAddress().getName());
        entity.setReceiverPhone(fareResp.getAddress().getPhone());
        entity.setReceiverPostCode(fareResp.getAddress().getPostCode());
        entity.setReceiverProvince(fareResp.getAddress().getProvince());
        entity.setReceiverRegion(fareResp.getAddress().getRegion());
        //?????????????????????????????????
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        entity.setAutoConfirmDay(7);
        entity.setConfirmStatus(0);
        return entity;
    }

    /**
     * ?????????????????????
     *
     * @param orderSn ??????sn
     * @return {@link List}<{@link OrderItemEntity}>
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //???????????????????????????????????????
        List<OrderItemVo> currentUserCartItems = cartFeginService.getCurrentUserCartItems();
        if (currentUserCartItems != null && currentUserCartItems.size() > 0) {
            return currentUserCartItems.stream().map(cartItem -> {
                //????????????????????????
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                //???????????????
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * ????????????????????????
     *
     * @param cartItem ?????????
     * @return {@link OrderItemEntity}
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        //1?????????????????????buildOrderItems????????????
        //2????????????spu??????
        Long skuId = cartItem.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        //3????????????sku??????
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        itemEntity.setSkuAttrsVals(StringUtils.
                collectionToDelimitedString(cartItem.getSkuAttr(), ";"));
        itemEntity.setSkuQuantity(cartItem.getCount());
        //4???????????????[??????]
        //5???????????????
        itemEntity.setGiftGrowth(cartItem.getPrice().intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().intValue());
        //6???????????????????????????
        itemEntity.setPromotionAmount(new BigDecimal("0.0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));
        //?????????????????????????????? ??????-????????????
        BigDecimal origin = itemEntity.getSkuPrice()
                .multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        BigDecimal subtract = origin.subtract(itemEntity.getPromotionAmount())
                .subtract(itemEntity.getCouponAmount()).subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(subtract);
        return itemEntity;
    }

    /**
     * ????????????,?????????
     *
     * @param orderEntity  ????????????
     * @param itemEntities ????????????
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        BigDecimal total = new BigDecimal("0");
        BigDecimal coupon = new BigDecimal("0");
        BigDecimal integration = new BigDecimal("0");
        BigDecimal promotion = new BigDecimal("0");
        BigDecimal gift = new BigDecimal("0.0");
        BigDecimal growth = new BigDecimal("0.0");
        //???????????????????????????????????????
        for (OrderItemEntity entity : itemEntities) {
            coupon = coupon.add(entity.getCouponAmount());
            integration = integration.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            total = total.add(entity.getRealAmount());
            gift = gift.add(new BigDecimal(entity.getGiftIntegration().toString()));
            growth = growth.add(new BigDecimal(entity.getGiftGrowth().toString()));
        }
        //????????????
        orderEntity.setTotalAmount(total);
        //????????????
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);
        //?????????????????????
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setDeleteStatus(0);
    }


}