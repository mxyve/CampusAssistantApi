package top.xym.campusassistantapi.module.auth.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 重置密码DTO
 * @author 你的名字
 */
@Data
public class ResetPasswordDTO {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    /**
     * 短信验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

}