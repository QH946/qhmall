package com.qh.qhmall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qh.qhmall.product.dao.AttrGroupDao;
import com.qh.qhmall.product.entity.BrandEntity;
import com.qh.qhmall.product.service.BrandService;
import com.qh.qhmall.product.service.CategoryService;
import com.qh.qhmall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class QhmallProductApplicationTests {

    @Autowired
    AttrGroupDao attrGroupDao;

    @Test
    public void testGetAttrGroupWithAttrsBySpuId(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(13L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }


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
        list.forEach((item) -> {
            System.out.println(item);
        });
    }

    //    @Test
//    public void testUpload() throws FileNotFoundException {
//        // Endpoint以杭州为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-beijing.aliyuncs.com";
//        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//        String accessKeyId = "LTAI5t5ZBiEYocB7qkRRRvkd";
//        String accessKeySecret = "CB5M5aJdkp6dQYYfVBM41kx1nH5EQy";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        // 上传文件流。
//        InputStream inputStream = new FileInputStream("D:\\05_Doc\\Mall_Resources\\0d40c24b264aa511.jpg");
//
//        ossClient.putObject("qhmall-aurora", "0d40c24b264aa511.jpg", inputStream);
//
//        // 关闭OSSClient。
//        ossClient.shutdown();
//
//        System.out.println("上传成功...");
//    }
    @Test
    public void testFindPath() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径：{}", Arrays.asList(catelogPath));
    }

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void testStringRedisTemplate() {

        stringRedisTemplate.opsForValue().set("hello","world_" + UUID.randomUUID().toString());
        String hello = stringRedisTemplate.opsForValue().get("hello");
        System.out.println("之前保存的数据是：" + hello);
    }

}
