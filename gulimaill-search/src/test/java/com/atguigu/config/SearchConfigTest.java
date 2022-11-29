package com.atguigu.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.JsonpMapper;
import jakarta.json.spi.JsonProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
class Product implements Serializable {
    private String sku;
    private String name;
    private double price;
}

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchConfigTest {

    @Autowired
    private ElasticsearchClient client;

    @Test
    public void createHotelIndex() throws IOException {

        // 方法1 DSL
        Product product = new Product("bk-1", "City bike", 123.0);

        IndexResponse response = client.index(i -> i
                .index("products")
                .id(product.getSku())
                .document(product)
        );

        log.info("Indexed with version: {}", response.version());
        // ======================================================================

        // 方法2 使用 IndexRequest
        Product product2 = new Product("bk-2", "City bike2", 1230.0);

        IndexRequest<Product> request = IndexRequest.of(i -> i
                .index("products")
                .id(product2.getSku())
                .document(product2)
        );

        IndexResponse response2 = client.index(request);

        log.info("Indexed with version: {}", response2.version());
        // =========================================================================

        // 方法3 使用 构造器
        Product product3 = new Product("bk-3", "City bike3", 1233.0);

        IndexRequest.Builder<Product> indexReqBuilder = new IndexRequest.Builder<>();
        indexReqBuilder.index("product3");
        indexReqBuilder.id(product3.getSku());
        indexReqBuilder.document(product3);

        IndexResponse response3 = client.index(indexReqBuilder.build());

        log.info("Indexed with version: {}", response3.version());

        // 操作 json
        Reader input = new StringReader(
                "{'@timestamp': '2022-04-08T13:55:32Z', 'level': 'warn', 'message': 'Some log message'}"
                        .replace('\'', '"'));

        IndexRequest<JsonData> request2 = IndexRequest.of(i -> i
                .index("logs")
                .withJson(input)
        );

        IndexResponse response4 = client.index(request2);

        log.info("Indexed with version: {}", response4.version());
    }

    @Test
    public void createHotelIndex2() throws IOException {

        List<Product> products = fetchProducts();

        BulkRequest.Builder br = new BulkRequest.Builder();

        for (Product product5 : products) {
            br.operations(op -> op                //[1]
                    .index(idx -> idx             //[2]
                            .index("products")  //[3]
                            .id(product5.getSku())
                            .document(product5)
                    )
            );
        }

        BulkResponse result = client.bulk(br.build());

        // Log errors, if any
        if (result.errors()) {
            log.error("Bulk had errors");
            for (BulkResponseItem item : result.items()) {
                if (item.error() != null) {
                    log.error(item.error().reason());
                }
            }
        }

        // 操作 json
        Reader input2 = new StringReader(
                "{'@timestamp': '2022-04-08T13:55:32Z', 'level': 'warn', 'message': 'Some log message'}"
                        .replace('\'', '"'));

        Reader input3 = new StringReader(
                "{'@timestamp': '2022-04-08T13:55:32Z', 'level': 'warn', 'message': 'Some log message'}"
                        .replace('\'', '"'));

        JsonpMapper jsonpMapper = client._transport().jsonpMapper();
        JsonProvider jsonProvider = jsonpMapper.jsonProvider();

        List<JsonData> jsonData = new ArrayList<>();
        jsonData.add(JsonData.from(jsonProvider.createParser(input2), jsonpMapper));
        jsonData.add(JsonData.from(jsonProvider.createParser(input3), jsonpMapper));

        BulkRequest.Builder br2 = new BulkRequest.Builder();

        jsonData.forEach(j -> br2.operations(op -> op
                .index(idx -> idx
                        .index("logs")
                        .document(jsonData)
                )
        ));
    }


    @Test
    public void test() throws IOException {

        Product product = new Product("bk-1", "bike", 123.0);

        BulkRequest.Builder br = new BulkRequest.Builder();

        // IndexResponse response = client.index(i -> i
        //         .index("products")
        //         .id(product.getSku())
        //         .document(product)
        // );

        br.operations(op -> op
                .index(idx -> idx
                        .index("products")
                        .id(product.getSku())
                        .document(product)
                )
        );

        client.bulk(br.build());

        // log.info("Indexed with version: {}", response.version());
    }

    @Test
    public void test2() throws IOException {

        String searchText = "bike";

        Query query = MatchQuery.of(m -> m
                .field("name")
                .query(searchText)
        )._toQuery();

        SearchResponse<Product> response = client.search(b -> b
                        .index("products")
                        .size(10)
                        .query(query)
                        .aggregations("sku", a -> a
                                .terms(h -> h
                                        .field("sku.keyword")
                                )
                        ),
                Product.class
        );

        List<StringTermsBucket> buckets = response.aggregations()
                .get("sku")
                .sterms()
                .buckets().array();

        for (StringTermsBucket bucket: buckets) {
            log.info("There are " + bucket.key()._toJsonString());
        }
    }

    private List<Product> fetchProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("bk-1", "bike", 123));
        products.add(new Product("bk-2", "bike", 123));
        products.add(new Product("bk-3", "bike", 123));
        return products;
    }
}