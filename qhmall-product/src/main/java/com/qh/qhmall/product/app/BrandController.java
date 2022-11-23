package com.qh.qhmall.product.app;

import com.qh.common.utils.PageUtils;
import com.qh.common.utils.R;
import com.qh.common.valid.AddGroup;
import com.qh.common.valid.UpdateGroup;
import com.qh.common.valid.UpdateStatusGroup;
import com.qh.qhmall.product.entity.BrandEntity;
import com.qh.qhmall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 品牌
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 查询品牌
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 获取品牌信息
     */
    @GetMapping ("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);
        return R.ok().put("brand", brand);
    }

    /**
     * 批量获取品牌信息
     *
     * @param brandIds 品牌标识
     * @return {@link R}
     */
    @GetMapping("/infos")
    public R brandInfo(@RequestParam("brandIds") List<Long> brandIds){
        List<BrandEntity> brand =brandService.getBrandsByIds(brandIds);
        return R.ok().put( "brand", brand);
    }

    /**
     * 新增品牌
     */
    @PostMapping("/save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand) {
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改品牌信息
     */
    @PostMapping("/update")
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand) {
        brandService.updateDetail(brand);
        return R.ok();
    }

    /**
     * 修改品牌状态
     */
    @PostMapping("/update/status")
    public R updateStatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand) {
        brandService.updateById(brand);
        return R.ok();
    }

    /**
     * 删除品牌
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));
        return R.ok();
    }

}
