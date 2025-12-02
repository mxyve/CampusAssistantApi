package top.xym.campusassistantapi.infrastructure.sms;

/**
 * 短信服务提供者接口
 */
public interface SmsProvider {
    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     * @return 是否发送成功
     */
    boolean sendSms(String mobile);
}
