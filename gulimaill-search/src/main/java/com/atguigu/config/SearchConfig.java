package com.atguigu.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 空~
 * 2022/11/26 周六 16:04
 **/
@Configuration
public class SearchConfig {

    @Value("${search.http_host}")
    private String searchHost;

    @Value("${search.http_port}")
    private int port;

    @Bean
    public ElasticsearchClient configClint() {
        // Create the low-level client
        RestClient restClient = RestClient.builder(
                new HttpHost(searchHost, port)).build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client
        return new ElasticsearchClient(transport);
    }
}
