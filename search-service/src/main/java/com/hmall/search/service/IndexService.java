package com.hmall.search.service;

import com.hmall.search.domain.po.ItemDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * 索引初始化服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IndexService implements ApplicationRunner {

    private final RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
            HttpHost.create("http://192.168.80.129:9200")));

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createIndexIfNotExists();
    }

    /**
     * 创建索引（如果不存在）
     */
    public void createIndexIfNotExists() {
        try {
            String indexName = "hmall";
            
            // 检查索引是否存在
            GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
            boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
            
            if (exists) {
                log.info("索引 {} 已存在", indexName);
                return;
            }
            
            // 创建索引
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            
            // 设置索引映射
            String mapping = "{" +
                "\"mappings\": {" +
                    "\"properties\": {" +
                        "\"id\": {" +
                            "\"type\": \"long\"" +
                        "}," +
                        "\"name\": {" +
                            "\"type\": \"text\"," +
                            "\"analyzer\": \"ik_max_word\"" +
                        "}," +
                        "\"image\": {" +
                            "\"type\": \"keyword\"," +
                            "\"index\": false" +
                        "}," +
                        "\"category\": {" +
                            "\"type\": \"keyword\"" +
                        "}," +
                        "\"brand\": {" +
                            "\"type\": \"keyword\"" +
                        "}," +
                        "\"sold\": {" +
                            "\"type\": \"integer\"" +
                        "}," +
                        "\"commentCount\": {" +
                            "\"type\": \"integer\"" +
                        "}," +
                        "\"isAD\": {" +
                            "\"type\": \"boolean\"" +
                        "}," +
                        "\"status\": {" +
                            "\"type\": \"integer\"" +
                        "}," +
                        "\"price\": {" +
                            "\"type\": \"integer\"" +
                        "}," +
                        "\"updateTime\": {" +
                            "\"type\": \"date\"" +
                        "}" +
                    "}" +
                "}" +
            "}";
            
            createIndexRequest.source(mapping, XContentType.JSON);
            
            client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            log.info("索引 {} 创建成功", indexName);
            
        } catch (Exception e) {
            log.error("创建索引失败", e);
        }
    }
    
    /**
     * 删除索引
     */
    public void deleteIndex() {
        try {
            String indexName = "hmall";
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
            client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            log.info("索引 {} 删除成功", indexName);
        } catch (Exception e) {
            log.error("删除索引失败", e);
        }
    }
}