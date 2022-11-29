package com.atguigu.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.atguigu.common.to.SkuEsModel;
import com.atguigu.constant.EsConstant;
import com.atguigu.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 空~
 * 2022/11/29 周二 9:39
 **/
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    private ElasticsearchClient esRestClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {

        //1.在es中建立索引，建立号映射关系（doc/json/product-mapping.json）

        //2. 在ES中保存这些数据

        BulkRequest.Builder br = new BulkRequest.Builder();

        for (SkuEsModel skuEsModel : skuEsModels) {
            br.operations(op -> op                //[1]
                    .index(idx -> idx             //[2]
                            .index(EsConstant.PRODUCT_INDEX)  //[3]
                            .id(skuEsModel.getSkuId().toString())
                            .document(skuEsModel)
                    )
            );
        }

        // 允许在单个请求中执行多个索引/更新/删除操作。
        BulkResponse bulk = esRestClient.bulk(br.build());

        //TODO 如果批量错误
        boolean hasFailures = bulk.errors();

        List<String> collect = bulk.items().stream().map(BulkResponseItem::id).collect(Collectors.toList());

        log.info("商品上架完成：{}", collect);

        return hasFailures;
    }
}
