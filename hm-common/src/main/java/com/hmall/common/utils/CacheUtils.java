package com.hmall.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Redis缓存工具类
 * 提供Cache Aside和延迟双删等缓存策略
 */
@Slf4j
public class CacheUtils {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
    
    public CacheUtils(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 设置缓存（带TTL随机化，防止缓存雪崩）
     */
    public void set(String key, Object value, Long time, TimeUnit unit) {
        // TTL随机化，避免缓存雪崩
        long randomTime = time + (long) (Math.random() * time * 0.5);
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), randomTime, unit);
    }

    /**
     * 设置逻辑过期缓存
     */
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * Cache Aside模式 - 缓存穿透解决方案（适用于搜索场景，支持复杂泛型）
     * 适用场景：搜索结果缓存，支持PageDTO<ItemDoc>等复杂泛型类型
     */
    public <R> R queryWithCacheAside(
            String cacheKey, TypeReference<R> typeReference, Supplier<R> dbFallback, Long time) {
        
        // 1. 从redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(cacheKey);
        
        // 2. 判断是否存在
        if (StrUtil.isNotBlank(json)) {
            log.debug("缓存命中: {}", cacheKey);
            try {
                return objectMapper.readValue(json, typeReference);
            } catch (Exception e) {
                log.error("缓存反序列化失败: {}", cacheKey, e);
                // 反序列化失败，删除缓存并查询数据库
                stringRedisTemplate.delete(cacheKey);
            }
        }
        
        // 判断命中的是否是空值
        if (json != null) {
            log.debug("缓存空值命中: {}", cacheKey);
            return null;
        }
        
        log.debug("缓存未命中，查询数据库: {}", cacheKey);
        
        // 3. 不存在，查询数据库
        R r = dbFallback.get();
        
        // 4. 不存在，写入空值到redis
        if (r == null) {
            stringRedisTemplate.opsForValue().set(cacheKey, "", 2L, TimeUnit.MINUTES);
            return null;
        }
        
        // 5. 存在，写入redis
        try {
            String jsonStr = objectMapper.writeValueAsString(r);
            this.set(cacheKey, jsonStr, time, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("缓存序列化失败: {}", cacheKey, e);
        }
        return r;
    }

    /**
     * Cache Aside模式 - 缓存穿透解决方案（适用于搜索场景）
     * 适用场景：搜索结果缓存，无需ID参数的查询
     */
    public <R> R queryWithCacheAside(
            String cacheKey, Class<R> type, Supplier<R> dbFallback, Long time) {
        
        // 1. 从redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(cacheKey);
        
        // 2. 判断是否存在
        if (StrUtil.isNotBlank(json)) {
            log.debug("缓存命中: {}", cacheKey);
            return JSONUtil.toBean(json, type);
        }
        
        // 判断命中的是否是空值
        if (json != null) {
            log.debug("缓存空值命中: {}", cacheKey);
            return null;
        }
        
        log.debug("缓存未命中，查询数据库: {}", cacheKey);
        
        // 3. 不存在，查询数据库
        R r = dbFallback.get();
        
        // 4. 不存在，写入空值到redis
        if (r == null) {
            stringRedisTemplate.opsForValue().set(cacheKey, "", 2L, TimeUnit.MINUTES);
            return null;
        }
        
        // 5. 存在，写入redis - 直接存储对象，避免重复序列化
        this.set(cacheKey, r, time, TimeUnit.MINUTES);
        return r;
    }

    /**
     * Cache Aside模式 - 缓存穿透解决方案
     * 适用场景：读多写少，对短暂不一致容忍度较高的查询
     */
    public <R, ID> R queryWithCacheAside(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        
        String key = keyPrefix + id;
        
        // 1. 从redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        
        // 2. 判断是否存在
        if (StrUtil.isNotBlank(json)) {
            log.debug("缓存命中: {}", key);
            return JSONUtil.toBean(json, type);
        }
        
        // 判断命中的是否是空值
        if (json != null) {
            log.debug("缓存空值命中: {}", key);
            return null;
        }
        
        log.debug("缓存未命中，查询数据库: {}", key);
        
        // 3. 不存在，根据id查询数据库
        R r = dbFallback.apply(id);
        
        // 4. 不存在，写入空值到redis
        if (r == null) {
            stringRedisTemplate.opsForValue().set(key, "", 2L, TimeUnit.MINUTES);
            return null;
        }
        
        // 5. 存在，写入redis
        this.set(key, r, time, unit);
        return r;
    }

    /**
     * Cache Aside模式 - 缓存击穿解决方案（互斥锁）
     * 适用场景：热点数据查询，防止缓存击穿
     */
    public <R, ID> R queryWithMutex(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        
        String key = keyPrefix + id;
        
        // 1. 从redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        
        // 2. 判断是否存在
        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, type);
        }
        
        // 判断命中的是否是空值
        if (json != null) {
            return null;
        }
        
        // 3. 实现缓存重建
        String lockKey = "lock:" + key;
        R r = null;
        try {
            // 3.1 获取互斥锁
            boolean isLock = tryLock(lockKey);
            
            // 3.2 判断是否获取成功
            if (!isLock) {
                // 3.3 失败，则休眠并重试
                Thread.sleep(50);
                return queryWithMutex(keyPrefix, id, type, dbFallback, time, unit);
            }
            
            // 3.4 成功，根据id查询数据库
            r = dbFallback.apply(id);
            
            // 3.5 不存在，写入空值到redis
            if (r == null) {
                stringRedisTemplate.opsForValue().set(key, "", 2L, TimeUnit.MINUTES);
                return null;
            }
            
            // 3.6 存在，写入redis
            this.set(key, r, time, unit);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取缓存锁被中断", e);
        } finally {
            // 4. 释放互斥锁
            unlock(lockKey);
        }
        
        return r;
    }

    /**
     * 延迟双删策略
     * 适用场景：数据一致性要求极高的写操作（如库存扣减、订单状态更新）
     */
    public void delayedDoubleDelete(String key, Runnable updateOperation) {
        delayedDoubleDelete(key, updateOperation, 500L);
    }

    /**
     * 延迟双删策略（自定义延迟时间）
     */
    public void delayedDoubleDelete(String key, Runnable updateOperation, Long delayMs) {
        log.debug("执行延迟双删策略: {}", key);
        
        // 1. 删除缓存
        stringRedisTemplate.delete(key);
        log.debug("第一次删除缓存: {}", key);
        
        try {
            // 2. 执行更新操作
            updateOperation.run();
            log.debug("执行数据库更新操作完成");
        } catch (Exception e) {
            log.error("数据库更新操作失败", e);
            throw e;
        }
        
        // 3. 延迟删除缓存
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(delayMs);
                stringRedisTemplate.delete(key);
                log.debug("延迟删除缓存完成: {}, 延迟时间: {}ms", key, delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("延迟删除缓存被中断: {}", key, e);
            } catch (Exception e) {
                log.error("延迟删除缓存失败: {}", key, e);
            }
        }, CACHE_REBUILD_EXECUTOR);
    }

    /**
     * 批量删除缓存
     */
    public void deleteBatch(String... keys) {
        if (keys != null && keys.length > 0) {
            stringRedisTemplate.delete(CollUtil.newArrayList(keys));
            log.debug("批量删除缓存: {}", String.join(", ", keys));
        }
    }

    /**
     * 删除匹配模式的缓存
     */
    public void deleteByPattern(String pattern) {
        var keys = stringRedisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
            log.debug("按模式删除缓存: {}, 删除数量: {}", pattern, keys.size());
        }
    }

    /**
     * 获取缓存
     */
    public <T> T get(String key, Class<T> type) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(json)) {
            return null;
        }
        return JSONUtil.toBean(json, type);
    }

    /**
     * 判断缓存是否存在
     */
    public boolean exists(String key) {
        return BooleanUtil.isTrue(stringRedisTemplate.hasKey(key));
    }

    /**
     * 设置缓存过期时间
     */
    public boolean expire(String key, long time, TimeUnit unit) {
        return BooleanUtil.isTrue(stringRedisTemplate.expire(key, time, unit));
    }

    /**
     * 获取分布式锁
     */
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 释放分布式锁
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 根据单个缓存键删除缓存
     * @param key 缓存键（完整键，无需拼接前缀）
     */
    public void deleteByKey(String key) {
        // 避免空键无效调用，增加日志提醒
        if (StrUtil.isBlank(key)) {
            log.debug("删除缓存失败：缓存键为空");
            return;
        }
        // 调用RedisTemplate删除指定缓存键
        Boolean deleteResult = stringRedisTemplate.delete(key);
        // 日志记录删除结果（成功/失败），便于排查
        if (BooleanUtil.isTrue(deleteResult)) {
            log.debug("根据键删除缓存成功：{}", key);
        } else {
            log.debug("根据键删除缓存失败（缓存不存在或已过期）：{}", key);
        }
    }
    /**
     * Redis数据封装类（用于逻辑过期）
     */
    public static class RedisData {
        private LocalDateTime expireTime;
        private Object data;

        public LocalDateTime getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(LocalDateTime expireTime) {
            this.expireTime = expireTime;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}