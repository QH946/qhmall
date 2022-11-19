package com.qh.qhmall.search.controller;


import com.qh.qhmall.search.service.MallSearchService;
import com.qh.qhmall.search.vo.SearchParam;
import com.qh.qhmall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * 搜索控制器
 *
 * @author 清欢
 * @date 2022/11/19 14:13:09
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;


    /**
     * 跳转list页面
     * SpringMVC自动将页面提交过来的所有请求参数封装成我们指定的对象
     *
     * @param param   参数
     * @param model   模型
     * @param request 请求
     * @return {@link String}
     */
    @GetMapping(value = "/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {
        param.set_queryString(request.getQueryString());
        //根据传递来的页面的查询参数，去es中检索商品
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);
        return "list";
    }

}
