package com.hmall.item.controller;

import com.hmall.common.utils.CacheUtils;
import com.hmall.common.utils.CacheConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = "Redis缓存测试接口")
@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final StringRedisTemplate stringRedisTemplate;
    private final CacheUtils cacheUtils;

    @ApiOperation("测试Redis基本连接")
    @GetMapping("/redis/ping")
    public Map<String, Object> testRedisConnection() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 测试Redis连接
            String pong = stringRedisTemplate.getConnectionFactory()
                    .getConnection().ping();
            
            result.put("success", true);
            result.put("message", "Redis连接成功");
            result.put("ping", pong);
            
            log.info("Redis连接测试成功: {}", pong);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Redis连接失败: " + e.getMessage());
            log.error("Redis连接测试失败", e);
        }
        return result;
    }

    @ApiOperation("测试Redis读写操作")
    @GetMapping("/redis/readwrite")
    public Map<String, Object> testRedisReadWrite() {
        Map<String, Object> result = new HashMap<>();
        try {
            String testKey = "test:redis:key";
            String testValue = "Hello Redis! " + System.currentTimeMillis();
            
            // 写入数据
            stringRedisTemplate.opsForValue().set(testKey, testValue, 60, TimeUnit.SECONDS);
            log.info("写入Redis: {} = {}", testKey, testValue);
            
            // 读取数据
            String readValue = stringRedisTemplate.opsForValue().get(testKey);
            log.info("从Redis读取: {} = {}", testKey, readValue);
            
            result.put("success", true);
            result.put("message", "Redis读写测试成功");
            result.put("writeValue", testValue);
            result.put("readValue", readValue);
            result.put("match", testValue.equals(readValue));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Redis读写测试失败: " + e.getMessage());
            log.error("Redis读写测试失败", e);
        }
        return result;
    }

    @ApiOperation("测试CacheUtils工具类")
    @GetMapping("/cache/utils")
    public Map<String, Object> testCacheUtils() {
        Map<String, Object> result = new HashMap<>();
        try {
            String cacheKey = CacheConstants.ITEM_CACHE_KEY_PREFIX + "test:123";
            
            // 使用CacheUtils进行缓存操作
            String cachedValue = cacheUtils.queryWithCacheAside(
                cacheKey,
                String.class,
                () -> {
                    log.info("模拟从数据库查询数据");
                    return "测试数据-" + System.currentTimeMillis();
                },
                CacheConstants.ITEM_CACHE_TTL
            );
            
            result.put("success", true);
            result.put("message", "CacheUtils测试成功");
            result.put("cacheKey", cacheKey);
            result.put("cachedValue", cachedValue);
            
            log.info("CacheUtils测试成功: {} = {}", cacheKey, cachedValue);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "CacheUtils测试失败: " + e.getMessage());
            log.error("CacheUtils测试失败", e);
        }
        return result;
    }

    @ApiOperation("查看Redis中的所有键")
    @GetMapping("/redis/keys")
    public Map<String, Object> getRedisKeys(@RequestParam(defaultValue = "*") String pattern) {
        Map<String, Object> result = new HashMap<>();
        try {
            var keys = stringRedisTemplate.keys(pattern);
            result.put("success", true);
            result.put("message", "获取Redis键成功");
            result.put("pattern", pattern);
            result.put("keys", keys);
            result.put("count", keys != null ? keys.size() : 0);
            
            log.info("Redis键查询成功，模式: {}, 数量: {}", pattern, keys != null ? keys.size() : 0);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取Redis键失败: " + e.getMessage());
            log.error("Redis键查询失败", e);
        }
        return result;
    }

    @ApiOperation("清理测试数据")
    @DeleteMapping("/redis/clean")
    public Map<String, Object> cleanTestData() {
        Map<String, Object> result = new HashMap<>();
        try {
            var testKeys = stringRedisTemplate.keys("test:*");
            if (testKeys != null && !testKeys.isEmpty()) {
                stringRedisTemplate.delete(testKeys);
                result.put("success", true);
                result.put("message", "清理测试数据成功");
                result.put("deletedCount", testKeys.size());
                log.info("清理了 {} 个测试键", testKeys.size());
            } else {
                result.put("success", true);
                result.put("message", "没有找到测试数据");
                result.put("deletedCount", 0);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "清理测试数据失败: " + e.getMessage());
            log.error("清理测试数据失败", e);
        }
        return result;
    }
}