package com.atguigu.search.service;

import com.atguigu.common.to.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author 空~
 * 2022/11/29 周二 9:39
 **/
public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
