package top.xym.campusassistantapi.module.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.xym.campusassistantapi.common.cache.RedisCache;
import top.xym.campusassistantapi.common.cache.RedisKeys;
import top.xym.campusassistantapi.common.exception.ServerException;
import top.xym.campusassistantapi.common.utils.JwtUtils;
import top.xym.campusassistantapi.infrastructure.sms.SmsProvider;
import top.xym.campusassistantapi.module.auth.model.dto.LoginDTO;
import top.xym.campusassistantapi.module.auth.model.dto.SmsLoginDTO;
import top.xym.campusassistantapi.module.user.mapper.UserMapper;
import top.xym.campusassistantapi.module.user.model.entity.UserEntity;
import top.xym.campusassistantapi.module.auth.model.vo.TokenVO;
import top.xym.campusassistantapi.module.auth.service.AuthService;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisCache redisCache;
    private final SmsProvider smsProvider;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    public TokenVO login(LoginDTO dto) {
        // 查询用户
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getUsername, dto.getUsername());
        UserEntity user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new ServerException("用户名不存在");
        }
        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ServerException("用户名或密码错误");
        }
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new ServerException("用户已被禁用");
        }
        // 生成 Token
        String token = jwtUtils.generateToken(user.getId());
        // 存储 Token 到 Redis
        String tokenKey = RedisKeys.getUserTokenKey(user.getId());
        redisCache.set(tokenKey, token, jwtExpiration / 1000, TimeUnit.SECONDS);
        return new TokenVO(token, jwtExpiration / 1000);
    }

    /**
     * 发送短信验证码
     */
    @Override
    public void sendSmsCode(String mobile) {
        // 调用短信服务发送验证码（内部会生成验证码并存储到 Redis）
        boolean success = smsProvider.sendSms(mobile);
        if (!success) {
            throw new ServerException("短信发送失败，请稍后重试");
        }
        log.info("短信验证码发送成功，手机号: {}", mobile);
    }

    /**
     * 短信验证码登录
     */
    @Override
    public TokenVO smsLogin(SmsLoginDTO dto) {
        // 从 Redis 获取验证码
        String key = RedisKeys.getSmsCodeKey(dto.getMobile());
        String code = redisCache.get(key, String.class);
        if (StrUtil.isBlank(code)) {
            throw new ServerException("验证码已过期");
        }
        if (!code.equals(dto.getCode())) {
            throw new ServerException("验证码错误");
        }
        // 删除验证码
        redisCache.delete(key);
        // 查询用户（根据手机号）
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getMobile, dto.getMobile());
        UserEntity user = userMapper.selectOne(wrapper);
        // 如果用户不存在，创建新用户（自动注册）
        if (user == null) {
            user = new UserEntity();
            user.setMobile(dto.getMobile());
            // 生成默认用户名（使用手机号）
            user.setUsername("user_" + dto.getMobile());
            // 默认启用
            user.setStatus(1);
            userMapper.insert(user);
            log.info("短信登录，创建新用户，mobile: {}", dto.getMobile());
        }
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new ServerException("用户已被禁用");
        }
        // 生成 Token
        String token = jwtUtils.generateToken(user.getId());
        // 存储 Token 到 Redis
        String tokenKey = RedisKeys.getUserTokenKey(user.getId());
        redisCache.set(tokenKey, token, jwtExpiration / 1000, TimeUnit.SECONDS);
        return new TokenVO(token, jwtExpiration / 1000);
    }

    /**
     * 登出
     */
    @Override
    public void logoutByUserId(Long userId) {
        if (userId != null) {
            // 删除 Redis 中的用户 Token
            String tokenKey = RedisKeys.getUserTokenKey(userId);
            redisCache.delete(tokenKey);
            log.info("用户 {} 登出成功", userId);
        }
    }

}
