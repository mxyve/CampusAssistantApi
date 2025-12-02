package top.xym.campusassistantapi.module.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户数据传输对象")
public class UserEditDTO implements Serializable {


    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "性别（0-未知，1-男，2-女）")
    private Integer gender;

}
