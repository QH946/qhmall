package com.qh.qhmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qh.common.utils.PageUtils;
import com.qh.common.utils.Query;
import com.qh.common.utils.R;
import com.qh.qhmall.ware.dao.WareInfoDao;
import com.qh.qhmall.ware.entity.WareInfoEntity;
import com.qh.qhmall.ware.feign.MemberFeignService;
import com.qh.qhmall.ware.service.WareInfoService;
import com.qh.qhmall.ware.vo.FareVo;
import com.qh.qhmall.ware.vo.MemberAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {


    @Autowired
    private MemberFeignService memberFeignService;

    /**
     * 查询仓库
     *
     * @param params 参数个数
     * @return {@link PageUtils}
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wareInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wareInfoEntityQueryWrapper.eq("id", key)
                    .or().like("name", key)
                    .or().like("address", key)
                    .or().like("areacode", key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wareInfoEntityQueryWrapper
        );
        return new PageUtils(page);
    }

    /**
     * 获取运费
     *
     * @param addrId addr id
     * @return {@link FareVo}
     */
    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R r = memberFeignService.addrInfo(addrId);
        MemberAddressVo data = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
        });
        if (data != null) {
            String phone = data.getPhone();
            //截取手机号最后一位作为运费（下标从0开始）
            String substring = phone.substring(phone.length() - 1);
            BigDecimal fare = new BigDecimal(substring);
            fareVo.setAddress(data);
            fareVo.setFare(fare);
            return fareVo;
        }
        return null;
    }

}