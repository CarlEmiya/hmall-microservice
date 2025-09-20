package com.hmall.search.service;

import com.hmall.search.domain.po.ItemDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据导入服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataImportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
            HttpHost.create("http://192.168.80.129:9200")));

    /**
     * 从MySQL导入商品数据到Elasticsearch
     */
    public void importItemsFromDatabase() {
        try {
            log.info("开始从数据库导入商品数据到Elasticsearch...");
            
            // 查询数据库中的商品数据
            String sql = "SELECT id, name, price, image,sold, comment_count, isAD,update_time, category, brand, status FROM item WHERE status = 1";
            log.info("执行SQL查询: {}", sql);
            List<Map<String, Object>> items = jdbcTemplate.queryForList(sql);
            log.info("查询结果数量: {}", items.size());
            
            if (items.isEmpty()) {
                log.warn("数据库中没有找到状态为1的商品数据，请检查数据库连接和数据");
                // 尝试查询所有商品数据
                String allSql = "SELECT COUNT(*) as total FROM item";
                List<Map<String, Object>> totalResult = jdbcTemplate.queryForList(allSql);
                log.info("数据库中商品总数: {}", totalResult.get(0).get("total"));
                return;
            }
            
            log.info("从数据库查询到 {} 条商品数据", items.size());
            
            // 打印前几条数据用于调试
            for (int i = 0; i < Math.min(3, items.size()); i++) {
                log.info("商品数据[{}]: {}", i, items.get(i));
            }
            
            // 批量导入到Elasticsearch
            BulkRequest bulkRequest = new BulkRequest();
            
            // 定义日期时间格式化器
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            
            for (Map<String, Object> item : items) {
                ItemDoc itemDoc = new ItemDoc();
                itemDoc.setId(((Number) item.get("id")).longValue());
                itemDoc.setName((String) item.get("name"));
                itemDoc.setPrice(((Number) item.get("price")).intValue());
                itemDoc.setCategory((String) item.get("category"));
                itemDoc.setBrand((String) item.get("brand"));
                itemDoc.setStatus(((Number) item.get("status")).intValue());
                itemDoc.setImage((String) item.get("image"));
                itemDoc.setSold(((Number) item.get("sold")).intValue());
                itemDoc.setCommentCount(((Number) item.get("comment_count")).intValue());
                itemDoc.setIsAD((Boolean) item.get("isAD"));
                
                // 正确处理LocalDateTime到String的转换
                Object updateTimeObj = item.get("update_time");
                if (updateTimeObj instanceof LocalDateTime) {
                    itemDoc.setUpdateTime(((LocalDateTime) updateTimeObj).format(formatter));
                } else if (updateTimeObj instanceof String) {
                    itemDoc.setUpdateTime((String) updateTimeObj);
                } else {
                    // 如果是其他类型，转换为字符串
                    itemDoc.setUpdateTime(updateTimeObj != null ? updateTimeObj.toString() : null);
                }

                
                // 创建索引请求 - 使用Map方式构建JSON
                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("id", itemDoc.getId());
                jsonMap.put("name", itemDoc.getName());
                jsonMap.put("image", itemDoc.getImage());
                jsonMap.put("category", itemDoc.getCategory());
                jsonMap.put("brand", itemDoc.getBrand());
                jsonMap.put("sold", itemDoc.getSold());
                jsonMap.put("commentCount", itemDoc.getCommentCount());
                jsonMap.put("isAD", itemDoc.getIsAD());
                jsonMap.put("status", itemDoc.getStatus());
                jsonMap.put("price", itemDoc.getPrice());
                jsonMap.put("updateTime", itemDoc.getUpdateTime());
                
                IndexRequest indexRequest = new IndexRequest("hmall")
                        .id(itemDoc.getId().toString())
                        .source(jsonMap);
                
                bulkRequest.add(indexRequest);

                System.out.println("成功添加商品数据"+itemDoc.getId());

            }
            
            // 执行批量导入
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            
            if (bulkResponse.hasFailures()) {
                log.error("批量导入部分失败: {}", bulkResponse.buildFailureMessage());
            } else {
                log.info("成功导入 {} 条商品数据到Elasticsearch", items.size());
            }
            
        } catch (Exception e) {
            log.error("导入数据失败", e);
        }
    }
    

    
    /**
     * 清空Elasticsearch中的所有数据
     */
    public void clearElasticsearchData() {
        try {
            log.info("开始清空Elasticsearch数据...");
            
            // 删除并重新创建索引
            IndexService indexService = new IndexService();
            indexService.deleteIndex();
            Thread.sleep(1000); // 等待删除完成
            indexService.createIndexIfNotExists();
            
            log.info("Elasticsearch数据清空完成");
        } catch (Exception e) {
            log.error("清空Elasticsearch数据失败", e);
        }
    }
}