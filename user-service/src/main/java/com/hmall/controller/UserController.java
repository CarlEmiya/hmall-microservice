package com.hmall.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmall.api.domain.dto.LoginFormDTO;
import com.hmall.api.domain.po.User;
import com.hmall.api.domain.vo.UserLoginVO;
import com.hmall.service.IUserService;
import com.hmall.common.utils.CacheUtils;
import com.hmall.common.utils.CacheConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户相关接口")
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final CacheUtils cacheUtils;

    @ApiOperation("用户登录接口")
    @PostMapping("login")
    public UserLoginVO login(@RequestBody @Validated LoginFormDTO loginFormDTO){
        return userService.login(loginFormDTO);
    }

    @ApiOperation("扣减余额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pw", value = "支付密码"),
            @ApiImplicitParam(name = "amount", value = "支付金额")
    })
    @PutMapping("/money/deduct")
    public void deductMoney(@RequestParam("pw") String pw,@RequestParam("amount") Integer amount){
        userService.deductMoney(pw, amount);
    }

    //根据id查用户
    @ApiOperation("根据id查用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id")
    })
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id){
        log.info("查询用户信息，用户ID: {}", id);
        
        // 构建缓存键
        String cacheKey = CacheConstants.USER_KEY_PREFIX + id;
        
        // 使用Cache Aside模式查询用户信息
        return cacheUtils.queryWithCacheAside(
            cacheKey,
            User.class,
            () -> {
                log.info("从数据库查询用户信息，用户ID: {}", id);
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", id);
                return userService.getOne(queryWrapper);
            },
            CacheConstants.USER_TTL
        );
    }
}