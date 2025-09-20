package com.hmall.cart.service.impl;


import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hmall.api.client.ItemClient;


import com.hmall.api.domain.dto.CartFormDTO;
import com.hmall.api.domain.dto.ItemDTO;
import com.hmall.api.domain.po.Cart;
import com.hmall.api.domain.vo.CartVO;
import com.hmall.cart.config.CartProperties;

import com.hmall.cart.mapper.CartMapper;
import com.hmall.cart.service.ICartService;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.BeanUtils;
import com.hmall.common.utils.CacheUtils;
import com.hmall.common.utils.CollUtils;
import com.hmall.common.utils.UserContext;
import com.hmall.common.utils.CacheConstants;
import java.util.function.Supplier;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单详情表 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {


    @Autowired
    private RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;
    private final ItemClient itemClient;
    private final CacheUtils cacheUtils;
    private final CartProperties cartProperties;

    @Override
    public void addItem2Cart(CartFormDTO cartFormDTO) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();
        log.info("添加商品到购物车，用户ID: {}, 商品ID: {}", userId, cartFormDTO.getItemId());

        // 2.验证必要字段
        if (cartFormDTO.getItemId() == null) {
            throw new BizIllegalException("商品ID不能为空");
        }
        if (StrUtil.isBlank(cartFormDTO.getName())) {
            throw new BizIllegalException("商品名称不能为空");
        }
        if (cartFormDTO.getPrice() == null) {
            throw new BizIllegalException("商品价格不能为空");
        }
        if (StrUtil.isBlank(cartFormDTO.getImage())) {
            throw new BizIllegalException("商品图片不能为空");
        }

        // 3.判断是否已经存在
        if (checkItemExists(cartFormDTO.getItemId(), userId)) {
            // 3.1.存在，则更新数量
            baseMapper.updateNum(cartFormDTO.getItemId(), userId);
            // 清除购物车缓存
            clearCartCache(userId);
            return;
        }
        // 3.2.不存在，判断是否超过购物车数量
        checkCartsFull(userId);

        // 4.新增购物车条目
        // 4.1.转换PO
        Cart cart = BeanUtils.copyBean(cartFormDTO, Cart.class);
        // 4.2.保存当前用户
        cart.setUserId(userId);
        // 4.3.保存到数据库
        save(cart);
        
        // 清除购物车缓存
        clearCartCache(userId);
        log.info("商品添加到购物车成功，用户ID: {}, 商品ID: {}", userId, cartFormDTO.getItemId());
    }


    @Override
    public List<CartVO> queryMyCarts() {
        Long userId = UserContext.getUser();
        if (userId == null) {
            log.warn("查询购物车失败：当前用户未登录");
            return CollUtils.emptyList();
        }
        log.info("查询用户购物车，用户ID: {}", userId);

        // 构建缓存键
        String cacheKey = CacheConstants.CART_KEY_PREFIX + userId;
        
        // 使用缓存查询
        @SuppressWarnings("unchecked")
        List<CartVO> result = (List<CartVO>) cacheUtils.queryWithCacheAside(
                cacheKey,
                List.class,
                () -> {
                    // 查询数据库
                    List<Cart> carts = lambdaQuery().eq(Cart::getUserId, userId).list();
                    if (CollUtils.isEmpty(carts)) {
                        return new ArrayList<>();
                    }
                    // 转换为VO并处理商品信息
                    List<CartVO> vos = BeanUtils.copyList(carts, CartVO.class);
                    handleCartItems(vos);
                    return vos;
                },
                CacheConstants.CART_TTL
        );
        return result;
    }



    private void handleCartItems(List<CartVO> vos) {
        // TODO 1.获取商品id
        Set<Long> itemIds = vos.stream().map(CartVO::getItemId).collect(Collectors.toSet());
        // 2.查询商品
        // List<ItemDTO> items = itemService.queryItemByIds(itemIds);
        // 2.1.利用RestTemplate发起http请求，得到http的响应

        List<ItemDTO> items = itemClient.queryItemByIds(itemIds);
        if (CollUtils.isEmpty(items)) {
            return;
        }
        // 3.转为 id 到 item的map
        Map<Long, ItemDTO> itemMap = items.stream().collect(Collectors.toMap(ItemDTO::getId, Function.identity()));
        // 4.写入vo
        for (CartVO v : vos) {
            ItemDTO item = itemMap.get(v.getItemId());
            if (item == null) {
                continue;
            }
            v.setNewPrice(item.getPrice());
            v.setStatus(item.getStatus());
            v.setStock(item.getStock());
        }
    }


    @Override
    public void removeByItemIds(Collection<Long> itemIds) {
        // 获取当前用户ID
        Long userId = UserContext.getUser();
        log.info("删除购物车商品，用户ID: {}, 商品IDs: {}", userId, itemIds);
        
        // 1.构建删除条件，userId和itemId
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<Cart>();
        queryWrapper.lambda()
                .eq(Cart::getUserId, userId)
                .in(Cart::getItemId, itemIds);
        // 2.删除
        remove(queryWrapper);
        
        // 清除购物车缓存
        clearCartCache(userId);
        log.info("购物车商品删除成功，用户ID: {}, 商品IDs: {}", userId, itemIds);
    }

    //根据用户id和商品ids来删除购物车中的商品
    public Boolean removeByItemIdsAndUserId(Long userId, Collection<Long> itemIds) {
        log.info("根据用户ID和商品IDs删除购物车商品，用户ID: {}, 商品IDs: {}", userId, itemIds);
        
        // 1.构建删除条件，userId和itemId
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<Cart>();
        queryWrapper.lambda()
                .eq(Cart::getUserId, userId)
                .in(Cart::getItemId, itemIds);
        // 2.删除
        Boolean result = remove(queryWrapper);
        
        // 清除购物车缓存
        clearCartCache(userId);
        log.info("购物车商品删除完成，用户ID: {}, 商品IDs: {}, 结果: {}", userId, itemIds, result);
        
        return result;
    }

    /**
     * 清除用户购物车缓存
     */
    private void clearCartCache(Long userId) {
        String cacheKey = CacheConstants.CART_KEY_PREFIX + userId;
        // 使用CacheUtils正确删除缓存
        cacheUtils.deleteByKey(cacheKey);
        log.info("清除购物车缓存，缓存键: {}", cacheKey);
    }

    @Override
    public boolean updateById(Cart entity) {
        // 更新购物车数据
        boolean result = super.updateById(entity);
        if (result) {
            // 清除购物车缓存
            clearCartCache(entity.getUserId());
            log.info("更新购物车成功，用户ID: {}", entity.getUserId());
        }
        return result;
    }

    @Override
    public boolean removeById(Long id) {
        // 先查询购物车信息获取用户ID
        Cart cart = getById(id);
        boolean result = super.removeById(id);
        if (result && cart != null) {
            // 清除购物车缓存
            clearCartCache(cart.getUserId());
            log.info("删除购物车项成功，用户ID: {}, 购物车ID: {}", cart.getUserId(), id);
        }
        return result;
    }

    private void checkCartsFull(Long userId) {
        int count = lambdaQuery().eq(Cart::getUserId, userId).count();
        if (count >= cartProperties.getMaxAmount()) {
            throw new BizIllegalException(StrUtil.format("用户购物车不能超过{}个", cartProperties.getMaxAmount()));
        }
    }

    private boolean checkItemExists(Long itemId, Long userId) {
        int count = lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getItemId, itemId)
                .count();
        return count > 0;
    }
}