package com.hmall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.api.domain.dto.LoginFormDTO;
import com.hmall.api.domain.po.User;
import com.hmall.api.domain.vo.UserLoginVO;
import com.hmall.common.enums.UserStatus;

import com.hmall.common.exception.BadRequestException;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.exception.ForbiddenException;
import com.hmall.common.utils.UserContext;
import com.hmall.common.utils.CacheUtils;
import com.hmall.common.constants.CacheConstants;
import com.hmall.config.JwtProperties;


import com.hmall.mapper.UserMapper;
import com.hmall.service.IUserService;
import com.hmall.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;
    
    private final CacheUtils cacheUtils;

    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 1.数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 2.根据用户名或手机号查询
        User user = lambdaQuery().eq(User::getUsername, username).one();
        Assert.notNull(user, "用户名错误");
        // 3.校验是否禁用
        if (user.getStatus() == UserStatus.FROZEN) {
            throw new ForbiddenException("用户被冻结");
        }
        // 4.校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }
        // 5.生成TOKEN
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
        // 6.封装VO返回
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setBalance(user.getBalance());
        vo.setToken(token);
        return vo;
    }

    @Override
    public void deductMoney(String pw, Integer totalFee) {
        log.info("开始扣款");
        // 获取当前用户ID
        Long userId = UserContext.getUser();
        
        // 1.校验密码
        User user = getById(userId);
        if(user == null || !passwordEncoder.matches(pw, user.getPassword())){
            // 密码错误
            throw new BizIllegalException("用户密码错误");
        }

        // 2.尝试扣款
        try {
            baseMapper.updateMoney(userId, totalFee);
            // 清除用户缓存
            clearUserCache(userId);
            log.info("扣款成功，用户ID: {}, 扣款金额: {}", userId, totalFee);
        } catch (Exception e) {
            throw new RuntimeException("扣款失败，可能是余额不足！", e);
        }
        log.info("扣款成功");
    }

    @Override
    public boolean updateById(User entity) {
        // 更新用户信息
        boolean result = super.updateById(entity);
        if (result) {
            // 清除用户缓存
            clearUserCache(entity.getId());
            log.info("更新用户信息成功，用户ID: {}", entity.getId());
        }
        return result;
    }

    /**
     * 清除用户缓存
     */
    private void clearUserCache(Long userId) {
        String cacheKey = CacheConstants.USER_KEY + userId;
        cacheUtils.deleteByKey(cacheKey);
        log.info("清除用户缓存，缓存键: {}", cacheKey);
    }
}