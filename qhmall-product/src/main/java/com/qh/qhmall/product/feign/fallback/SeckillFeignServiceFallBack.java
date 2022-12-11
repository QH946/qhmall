package com.qh.qhmall.product.feign.fallback;


import com.qh.common.exception.BizCodeEnum;
import com.qh.common.utils.R;
import com.qh.qhmall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 秒杀服务回滚
 *
 * @author 清欢
 * @date 2022/12/11  18:46:35
 */
@Slf4j
@Component
public class SeckillFeignServiceFallBack implements SeckillFeignService {
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.info("熔断方法调用...getSkuSeckillInfo");
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(),BizCodeEnum.TOO_MANY_REQUEST.getMsg());
    }
}
