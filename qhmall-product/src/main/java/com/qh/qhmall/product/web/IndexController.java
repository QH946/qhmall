package com.qh.qhmall.product.web;


import com.qh.qhmall.product.entity.CategoryEntity;
import com.qh.qhmall.product.service.CategoryService;
import com.qh.qhmall.product.vo.Catalogs2Vo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * 页面跳转
 *
 * @author 清欢
 * @date 2022/11/16 13:13:36
 */
@Controller
public class IndexController {

    @Resource
    private CategoryService categoryService;


    @GetMapping(value = {"/", "index.html"})
    private String indexPage(Model model) {
        //查询所有的一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();
        model.addAttribute("categories", categoryEntities);
        return "index";
    }


    /**
     * 获取二级、三级分类数据
     *
     * @return {@link Map}<{@link String}, {@link List}<{@link Catalogs2Vo}>>
     */
    @GetMapping(value = "/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catalogs2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }


}
