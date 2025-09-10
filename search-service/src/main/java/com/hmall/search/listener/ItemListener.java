package com.hmall.search.listener;

import com.alibaba.fastjson.JSON;
import com.hmall.api.client.ItemClient;
import com.hmall.api.domain.dto.ItemDTO;
import com.hmall.common.utils.BeanUtils;


import com.hmall.search.domain.po.ItemDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 商品数据同步监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ItemListener {

    private final ItemClient itemClient;


    private final RestHighLevelClient client = new  RestHighLevelClient(RestClient.builder(
            HttpHost.create("http://192.168.80.129:9200")));



    /** 商品增加消息监听器 */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.add.queue", durable = "true"),
            exchange = @Exchange(name = "search.item.direct", type = "topic"),
            key = {"item.add"}
    ))
    public void listenItemQuery(Long itemId) {
        System.out.println("[ItemListener] === 接收到商品新增消息 ===");
        System.out.println("  - 商品ID: " + itemId);

        try {
            // 1. 根据商品id查询商品信息
            ItemDTO itemDTO = itemClient.queryItemById(itemId);
            if (itemDTO == null) {
                log.warn("商品不存在，商品ID: {}", itemId);
                return;
            }

            // 2. 转换为ItemDoc
            ItemDoc itemDoc = BeanUtils.copyBean(itemDTO, ItemDoc.class);

            String jsonItemDoc = JSON.toJSONString(itemDoc);

            IndexRequest request = new IndexRequest("hmall").id(itemDoc.getId().toString());

            request.source(jsonItemDoc, XContentType.JSON);

            client.index(request, RequestOptions.DEFAULT);

            System.out.println("[ItemListener] ✅ 商品索引增加成功");
            System.out.println("检索到RabbitMq发送的商品增加消息，存入信息："+jsonItemDoc);

        } catch (Exception e) {
            System.out.println("[ItemListener] ❌ 商品索引增加失败: " + e.getMessage());
            log.error("商品索引增加失败，商品ID: {}", itemId, e);
        }
    }


//    商品删除
        @RabbitListener(bindings = @QueueBinding(
                value = @Queue(name = "search.item.delete.queue", durable = "true"),
                exchange = @Exchange(name = "search.item.direct", type = "topic"),
                key = "item.delete"
        ))
        public void listenItemDelete(Long itemId) {
            System.out.println("[ItemListener] === 接收到商品删除消息 ===");
            System.out.println("  - 商品ID: " + itemId);

            try {
                DeleteRequest request = new DeleteRequest("items", itemId.toString());
                client.delete(request, RequestOptions.DEFAULT);

                System.out.println("[ItemListener] ✅ 商品索引删除成功");

            } catch (Exception e) {
                System.out.println("[ItemListener] ❌ 商品索引删除失败: " + e.getMessage());
                log.error("商品索引删除失败，商品ID: {}", itemId, e);
            }
        }

        //商品修改：
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.update.queue", durable = "true"),
            exchange = @Exchange(name = "search.item.direct", type = "topic"),
            key = {"item.update"}
    ))
    public void listenItemUpdate(Long itemId) {
        System.out.println("[ItemListener] === 接收到商品修改消息 ===");
        System.out.println("  - 商品ID: " + itemId);

        try {
            // 1. 根据商品id查询商品信息
            ItemDTO itemDTO = itemClient.queryItemById(itemId);
            if (itemDTO == null) {
                log.warn("商品不存在，商品ID: {}", itemId);
                return;
            }

            // 2. 转换为ItemDoc
            ItemDoc itemDoc = BeanUtils.copyBean(itemDTO, ItemDoc.class);

            String jsonItemDoc = JSON.toJSONString(itemDoc);

            UpdateRequest request = new UpdateRequest("items",itemDoc.toString());

            request.doc(jsonItemDoc, XContentType.JSON);

            client.update(request, RequestOptions.DEFAULT);

            System.out.println("[ItemListener] ✅ 商品索引修改成功");
            System.out.println("检索到RabbitMq发送的商品修改消息，修改信息："+jsonItemDoc);

        } catch (Exception e) {
            System.out.println("[ItemListener] ❌ 商品索引修改失败: " + e.getMessage());
            log.error("商品索引修改失败，商品ID: {}", itemId, e);
        }
    }
}