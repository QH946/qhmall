package com.qh.qhmall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.qhmall.coupon.dao.SeckillSessionDao;
import com.qh.qhmall.coupon.entity.SeckillSessionEntity;
import com.qh.qhmall.coupon.entity.SeckillSkuRelationEntity;
import com.qh.qhmall.coupon.service.SeckillSessionService;
import com.qh.qhmall.coupon.service.SeckillSkuRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询每场活动关联的秒杀商品
     *
     * @return {@link List}<{@link SeckillSessionEntity}>
     */
    @Override
    public List<SeckillSessionEntity> getLates3DaySession() {
        //查询三天内的秒杀活动
        List<SeckillSessionEntity> list = list(new QueryWrapper<SeckillSessionEntity>()
                .between("start_time", startTime(), endTime()));
        if (list != null && list.size() > 0) {
            return list.stream().map(session -> {
                Long id = session.getId();
                //查询并设置每场活动关联的秒杀商品
                List<SeckillSkuRelationEntity> relationEntities =
                        seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>()
                                .eq("promotion_session_id", id));
                session.setRelationSkus(relationEntities);
                return session;
            }).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    //计算最近三天的时间
    private String startTime() {
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        LocalDateTime start = LocalDateTime.of(now, min);
        String startTime = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("start:" + startTime);
        return startTime;
    }

    private String endTime() {
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(3);
        LocalTime max = LocalTime.MAX;
        LocalDateTime end = LocalDateTime.of(localDate, max);
        String endTime = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("end:" + endTime);
        return endTime;
    }

}