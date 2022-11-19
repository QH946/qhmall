package com.qh.qhmall.search.service;

import com.qh.qhmall.search.vo.SearchParam;
import com.qh.qhmall.search.vo.SearchResult;

public interface MallSearchService {

    /**
     * 搜索
     * 返回检索的结果，里面包含页面需要的所有信息
     *
     * @param param 参数
     * @return {@link SearchResult}
     */
    SearchResult search(SearchParam param);
}
