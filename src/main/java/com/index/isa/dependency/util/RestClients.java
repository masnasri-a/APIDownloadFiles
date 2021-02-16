package com.index.isa.dependency.util;

import com.index.isa.dependency.config.ElasticConfig;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RestClients {
    @Autowired
    ElasticConfig elasticConfig;
    public RestHighLevelClient createRestHighClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.180.221", 5200))
//        RestClientBuilder builder = RestClient.builder(new HttpHost(, 5200))
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                        return builder
                                .setConnectTimeout(60000)
                                .setSocketTimeout(300000);
                    }
                });
        return new RestHighLevelClient(builder);
    }
}