package top.xym.campusassistantapi.module.auth.service;

import top.xym.campusassistantapi.module.auth.model.dto.LoginDTO;
import top.xym.campusassistantapi.module.auth.model.dto.SmsLoginDTO;
import top.xym.campusassistantapi.module.auth.model.vo.TokenVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 账号密码登录
     *
     * @Param dto 登录信息
     * @return Token
     */
    TokenVO login(LoginDTO dto);

    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     */
    void sendSmsCode(String mobile);

    /**
     * 短信验证码登录
     *
     * @param dto 登录信息
     * @return Token
     */
    TokenVO smsLogin(SmsLoginDTO dto);

    /**
     * 根据用户 ID 登出
     *
     * @param userId 用户 ID
     */
    void logoutByUserId(Long userId);

}
