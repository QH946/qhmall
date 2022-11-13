package com.qh.qhmall.search.service;


import com.qh.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;


public interface ProductSaveService {

    /**
     * 上架商品
     *
     * @param skuEsModels sku es模型
     * @return boolean
     * @throws IOException ioexception
     */
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
