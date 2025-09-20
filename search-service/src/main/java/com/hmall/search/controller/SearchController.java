package com.hmall.search.controller;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import com.hmall.api.domain.dto.ItemDTO;

import com.hmall.common.utils.BeanUtils;
import com.hmall.common.utils.CacheUtils;
import com.hmall.common.constants.CacheConstants;
import com.hmall.common.utils.CacheKeyUtils;

import com.hmall.common.utils.CollUtils;
import com.hmall.search.domain.po.ItemDoc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import cn.hutool.core.util.StrUtil;
import com.hmall.common.domain.PageDTO;
import com.hmall.search.domain.dto.ItemPageQuery;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;

@Api(tags = "搜索相关接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final RestHighLevelClient client = new  RestHighLevelClient(RestClient.builder(
            HttpHost.create("http://192.168.80.129:9200")));
    
    private final CacheUtils cacheUtils;

    @ApiOperation("根据id搜索商品")
    @GetMapping("/{id}")
    public ItemDTO  getById(@PathVariable String id) throws IOException {
        GetRequest request = new GetRequest("hmall", id);
        request.id(id.toString());
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String source =  response.getSourceAsString();
        ItemDoc bean = JSONUtil.toBean(source, ItemDoc.class);   //将json转为ItemDoc对象
        ItemDTO dto = new ItemDTO();
        BeanUtils.copyProperties(bean, dto);
        return  dto;
    }


    @Test
    void testMatchAll() throws IOException {
        // 1.创建Request
        SearchRequest request = new SearchRequest("hmall");
        // 2.组织请求参数
        request.source().query(QueryBuilders.matchAllQuery());
        // 3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析响应
        handleResponse(response);
    }



    @Test
    void testBool() throws IOException {
        // 1.创建Request
        SearchRequest request = new SearchRequest("hmall");
        // 2.组织请求参数
        // 2.1.准备bool查询
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        // 2.2.关键字搜索
        bool.must(QueryBuilders.matchQuery("name", "脱脂牛奶"));
        // 2.3.品牌过滤
        bool.filter(QueryBuilders.termQuery("brand", "德亚"));
        // 2.4.价格过滤
        bool.filter(QueryBuilders.rangeQuery("price").lte(30000));
        request.source().query(bool);
        // 3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析响应
        handleResponse(response);
    }

    @Test
    void testPageAndSort() throws IOException {
        int pageNo = 1, pageSize = 5;

        // 1.创建Request
        SearchRequest request = new SearchRequest("hmall");
        // 2.组织请求参数
        // 2.1.搜索条件参数
        request.source().query(QueryBuilders.matchQuery("name", "森马"));
        // 2.2.排序参数
        request.source().sort("price", SortOrder.ASC);
        // 2.3.分页参数
        request.source().from((pageNo - 1) * pageSize).size(pageSize);
        // 3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析响应
        handleResponse(response);
    }


    @ApiOperation("商品搜索")
    @GetMapping("/list")
    public PageDTO<ItemDoc> search(ItemPageQuery query) throws IOException {
        log.info("商品搜索，参数: {}", query);
        
        // 构建缓存键
        String cacheKey = CacheKeyUtils.buildSearchKey(query.getKey(), query.getCategory(), 
                query.getBrand(), query.getMinPrice(), query.getMaxPrice(), 
                query.getPageNo(), query.getPageSize(), query.getSortBy(), query.getIsAsc());
        
        // 使用Cache Aside模式查询（支持复杂泛型）
        return cacheUtils.queryWithCacheAside(
                cacheKey,
                new TypeReference<PageDTO<ItemDoc>>() {},
                () -> {
                    try {
                        log.info("从Elasticsearch查询搜索结果");
                        return searchItems(query);
                    } catch (IOException e) {
                        log.error("搜索查询失败", e);
                        throw new RuntimeException(e);
                    }
                },
                CacheConstants.SEARCH_RESULT_TTL
        );
    }
    
    @ApiOperation("搜索过滤条件聚合")
    @PostMapping("/filters")
    public Map<String, List<String>> getFilters(@RequestBody ItemPageQuery query) throws IOException {
        log.info("获取搜索过滤条件，参数: {}", query);
        
        // 构建缓存键
        String cacheKey = CacheKeyUtils.buildSearchFiltersKey(query.getKey(), query.getCategory(), query.getBrand());
        
        // 使用Cache Aside模式查询（支持复杂泛型）
        return cacheUtils.queryWithCacheAside(
                cacheKey,
                new TypeReference<Map<String, List<String>>>() {},
                () -> {
                    try {
                        log.info("从Elasticsearch查询搜索过滤条件");
                        return getSearchFilters(query);
                    } catch (IOException e) {
                        log.error("搜索过滤条件查询失败", e);
                        throw new RuntimeException(e);
                    }
                },
                CacheConstants.SEARCH_FILTERS_TTL
        );
    }

    @ApiOperation("高亮搜索测试")
    @GetMapping("/testHighlight")
    public String testHighlight() throws IOException {
        // 1.创建Request
        SearchRequest request = new SearchRequest("hmall");
        // 2.组织请求参数
        // 2.1.query条件
        request.source().query(QueryBuilders.matchQuery("name", "森马"));
        // 2.2.高亮条件
        request.source().highlighter(
                SearchSourceBuilder.highlight()
                        .field("name")
                        .preTags("<em>")
                        .postTags("</em>")
        );
        // 3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析响应并返回结果
        return handleResponseAndReturn(response);
    }


    private void handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 1.获取总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到" + total + "条数据");
        // 2.遍历结果数组
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            // 3.得到_source，也就是原始json文档
            String source = hit.getSourceAsString();
            // 4.反序列化
            ItemDoc item = JSONUtil.toBean(source, ItemDoc.class);
            // 5.获取高亮结果
            Map<String, HighlightField> hfs = hit.getHighlightFields();
            if (CollUtils.isNotEmpty(hfs)) {
                // 5.1.有高亮结果，获取name的高亮结果
                HighlightField hf = hfs.get("name");
                if (hf != null) {
                    // 5.2.获取第一个高亮结果片段，就是商品名称的高亮值
                    String hfName = hf.getFragments()[0].string();
                    item.setName(hfName);
                }
            }
            System.out.println(item);
        }
    }

    private String handleResponseAndReturn(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 1.获取总条数
        long total = searchHits.getTotalHits().value;
        StringBuilder result = new StringBuilder();
        result.append("共搜索到").append(total).append("条数据\n");
        
        // 2.遍历结果数组
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            // 3.得到_source，也就是原始json文档
            String source = hit.getSourceAsString();
            // 4.反序列化
            ItemDoc item = JSONUtil.toBean(source, ItemDoc.class);
            // 5.获取高亮结果
            Map<String, HighlightField> hfs = hit.getHighlightFields();
            if (CollUtils.isNotEmpty(hfs)) {
                // 5.1.有高亮结果，获取name的高亮结果
                HighlightField hf = hfs.get("name");
                if (hf != null) {
                    // 5.2.获取第一个高亮结果片段，就是商品名称的高亮值
                    String hfName = hf.getFragments()[0].string();
                    item.setName(hfName);
                }
            }
            result.append(item.toString()).append("\n");
        }
        return result.toString();
    }

    /**
     * 商品搜索核心方法
     */
    private PageDTO<ItemDoc> searchItems(ItemPageQuery query) throws IOException {
        // 1.创建搜索请求
        SearchRequest request = new SearchRequest("hmall");
        
        // 2.构建查询条件
        BoolQueryBuilder boolQuery = buildQuery(query);
        
        // 3.构建function_score查询实现竞价排名
        FunctionScoreQueryBuilder.FilterFunctionBuilder[] functions = {
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.termQuery("isAD", true),
                        ScoreFunctionBuilders.weightFactorFunction(10)
                )
        };
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(boolQuery, functions);
        
        // 4.设置查询条件
        request.source().query(functionScoreQuery);
        
        // 5.设置分页
        int from = (query.getPageNo() - 1) * query.getPageSize();
        request.source().from(from).size(query.getPageSize());
        
        // 6.设置排序
        if (StrUtil.isNotBlank(query.getSortBy())) {
            SortOrder sortOrder = query.getIsAsc() ? SortOrder.ASC : SortOrder.DESC;
            request.source().sort(query.getSortBy(), sortOrder);
        } else {
            // 默认按更新时间降序排序
            request.source().sort("updateTime", SortOrder.DESC);
        }
        
        // 7.设置高亮
        if (StrUtil.isNotBlank(query.getKey())) {
            request.source().highlighter(
                    SearchSourceBuilder.highlight()
                            .field("name")
                            .preTags("<em>")
                            .postTags("</em>")
            );
        }
        
        // 8.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        
        // 9.解析响应
        return parseSearchResponse(response, query);
    }
    
    /**
     * 构建查询条件
     */
    private BoolQueryBuilder buildQuery(ItemPageQuery query) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 关键词搜索
        if (StrUtil.isNotBlank(query.getKey())) {
            boolQuery.must(QueryBuilders.matchQuery("name", query.getKey()));
        } else {
            boolQuery.must(QueryBuilders.matchAllQuery());
        }
        
        // 品牌过滤
        if (StrUtil.isNotBlank(query.getBrand())) {
            boolQuery.filter(QueryBuilders.termQuery("brand", query.getBrand()));
        }
        
        // 分类过滤
        if (StrUtil.isNotBlank(query.getCategory())) {
            boolQuery.filter(QueryBuilders.termQuery("category", query.getCategory()));
        }
        
        // 价格范围过滤
        if (query.getMinPrice() != null || query.getMaxPrice() != null) {
            var rangeQuery = QueryBuilders.rangeQuery("price");
            if (query.getMinPrice() != null) {
                rangeQuery.gte(query.getMinPrice());
            }
            if (query.getMaxPrice() != null) {
                rangeQuery.lte(query.getMaxPrice());
            }
            boolQuery.filter(rangeQuery);
        }
        
        // 只查询上架商品
        boolQuery.filter(QueryBuilders.termQuery("status", 1));
        
        return boolQuery;
    }
    
    /**
     * 解析搜索响应
     */
    private PageDTO<ItemDoc> parseSearchResponse(SearchResponse response, ItemPageQuery query) {
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        
        List<ItemDoc> items = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        
        for (SearchHit hit : hits) {
            // 获取原始文档
            String source = hit.getSourceAsString();
            ItemDoc item = JSONUtil.toBean(source, ItemDoc.class);
            
            // 处理高亮结果
            Map<String, HighlightField> hfs = hit.getHighlightFields();
            if (CollUtils.isNotEmpty(hfs)) {
                HighlightField hf = hfs.get("name");
                if (hf != null) {
                    String hfName = hf.getFragments()[0].string();
                    item.setName(hfName);
                }
            }
            
            items.add(item);
        }
        
        // 计算总页数
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        
        return new PageDTO<>(total, pages, items);
    }
    
    /**
     * 获取搜索过滤条件聚合结果
     */
    private Map<String, List<String>> getSearchFilters(ItemPageQuery query) throws IOException {
        // 1.创建搜索请求
        SearchRequest request = new SearchRequest("hmall");
        
        // 2.构建查询条件（不包括品牌和分类过滤，因为我们要聚合这些字段）
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 关键词搜索
        if (StrUtil.isNotBlank(query.getKey())) {
            boolQuery.must(QueryBuilders.matchQuery("name", query.getKey()));
        } else {
            boolQuery.must(QueryBuilders.matchAllQuery());
        }
        
        // 价格范围过滤
        if (query.getMinPrice() != null || query.getMaxPrice() != null) {
            var rangeQuery = QueryBuilders.rangeQuery("price");
            if (query.getMinPrice() != null) {
                rangeQuery.gte(query.getMinPrice());
            }
            if (query.getMaxPrice() != null) {
                rangeQuery.lte(query.getMaxPrice());
            }
            boolQuery.filter(rangeQuery);
        }
        
        // 只查询上架商品
        boolQuery.filter(QueryBuilders.termQuery("status", 1));
        
        // 3.设置查询条件和聚合
        request.source().query(boolQuery).size(0); // size(0)表示不返回文档，只返回聚合结果
        
        // 4.添加分类聚合
        request.source().aggregation(
                AggregationBuilders.terms("category_agg")
                        .field("category")
                        .size(20) // 最多返回20个分类
        );
        
        // 5.添加品牌聚合
        request.source().aggregation(
                AggregationBuilders.terms("brand_agg")
                        .field("brand")
                        .size(20) // 最多返回20个品牌
        );
        
        // 6.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        
        // 7.解析聚合结果
        return parseAggregationResponse(response);
    }
    
    /**
     * 解析聚合响应
     */
    private Map<String, List<String>> parseAggregationResponse(SearchResponse response) {
        Map<String, List<String>> filters = new HashMap<>();
        
        Aggregations aggregations = response.getAggregations();
        
        // 解析分类聚合
        Terms categoryTerms = aggregations.get("category_agg");
        List<String> categories = new ArrayList<>();
        for (Terms.Bucket bucket : categoryTerms.getBuckets()) {
            categories.add(bucket.getKeyAsString());
        }
        filters.put("category", categories);
        
        // 解析品牌聚合
        Terms brandTerms = aggregations.get("brand_agg");
        List<String> brands = new ArrayList<>();
        for (Terms.Bucket bucket : brandTerms.getBuckets()) {
            brands.add(bucket.getKeyAsString());
        }
        filters.put("brand", brands);
        
        return filters;
    }

    @Test
    void testAgg() throws IOException {
        // 1.创建Request
        SearchRequest request = new SearchRequest("items");
        // 2.准备请求参数
        BoolQueryBuilder bool = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("category", "手机"))
                .filter(QueryBuilders.rangeQuery("price").gte(300000));
        request.source().query(bool).size(0);
        // 3.聚合参数
        request.source().aggregation(
                AggregationBuilders.terms("brand_agg").field("brand").size(5)
        );
        // 4.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 5.解析聚合结果
        Aggregations aggregations = response.getAggregations();
        // 5.1.获取品牌聚合
        Terms brandTerms = aggregations.get("brand_agg");
        // 5.2.获取聚合中的桶
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        // 5.3.遍历桶内数据
        for (Terms.Bucket bucket : buckets) {
            // 5.4.获取桶内key
            String brand = bucket.getKeyAsString();
            System.out.print("brand = " + brand);
            long count = bucket.getDocCount();
            System.out.println("; count = " + count);
        }
    }

}