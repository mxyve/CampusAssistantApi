package top.xym.campusassistantapi.infrastructure.sms.impl;

import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xym.campusassistantapi.common.cache.RedisCache;
import top.xym.campusassistantapi.common.cache.RedisKeys;
import top.xym.campusassistantapi.infrastructure.sms.SmsProvider;

import java.util.concurrent.TimeUnit;

/**
 * 容联云短信服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RongLianSmsServiceImpl implements SmsProvider {
    private final RedisCache redisCache;

    @Override
    public boolean sendSms(String mobile) {

        // 生成 4 位验证码
        String code = RandomUtil.randomNumbers(4);

        log.info("【模拟发送】手机号: {}, 验证码: {}", mobile, code);

        // 存入 Redis，有效期 5 分钟
        String codeKey = RedisKeys.getSmsCodeKey(mobile);
        redisCache.set(codeKey, code, 5, TimeUnit.MINUTES);

        return true;
    }
}
