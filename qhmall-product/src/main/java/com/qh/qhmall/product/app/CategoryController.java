package com.qh.qhmall.product.app;

import com.qh.common.utils.R;
import com.qh.qhmall.product.entity.CategoryEntity;
import com.qh.qhmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


/**
 * 商品三级分类
 *
 * @author 清欢
 * @email qh@gmail.com
 * @date 2022-10-26 14:41:49
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查出所有分类以及子分类，以树形结构组装起来
     */
    @GetMapping("/list/tree")
    public R list() {
        List<CategoryEntity> entities = categoryService.listWithTree();
        return R.ok().put("data", entities);
    }


    /**
     * 根据分类id查询商品分类
     */
    @GetMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);
        return R.ok().put("data", category);
    }

    /**
     * 新增商品分类
     */
    @PostMapping("/save")
    public R save(@RequestBody CategoryEntity category) {
        categoryService.save(category);
        return R.ok();
    }

    /**
     * 级联更新分类的所有关联的数据
     */
    @PostMapping("/update")
    public R update(@RequestBody CategoryEntity category) {
        categoryService.updateCascade(category);
        return R.ok();
    }

    /**
     * 以拖拽方式批量修改分类位置
     *
     * @param category 类别
     * @return {@link R}
     */
    @PostMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity[] category) {
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }

    /**
     * 批量删除分类
     *
     * @RequestBody: 获取请求体，必须发送POST请求
     * * SpringMVC自动将请求体的数据（json），转为对应的对象
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] catIds) {
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
