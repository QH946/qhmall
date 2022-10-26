package com.qh.qhmall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qh.qhmall.product.entity.BrandEntity;
import com.qh.qhmall.product.service.BrandService;
import com.qh.qhmall.product.service.CategoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QhmallProductApplicationTests {


    @Autowired
    BrandService brandService;


    @Autowired
    CategoryService categoryService;

    @Test
    public void insertBrand() {
        BrandEntity brand = new BrandEntity();
        brand.setName("lq");
        brandService.save(brand);
        System.out.println("添加成功");
    }

    @Test
    public void deleteBrand() {
        brandService.removeById(13L);
        System.out.println("删除成功");
    }

    @Test
    public void updateBrand() {
        BrandEntity brand = new BrandEntity();
        brand.setBrandId(9L);
        brand.setDescript("中华有为");
        brandService.updateById(brand);
        System.out.println("修改成功");
    }

    @Test
    public void findBrand() {
        List<BrandEntity> list =
                brandService
                        .list(new QueryWrapper<BrandEntity>()
                                .eq("brand_id", 9L));
        list.forEach((item)->{
            System.out.println(item);
        });
    }

}
