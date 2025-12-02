package top.xym.campusassistantapi.module.auth.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Token 响应 VO
 */
@Data
@Schema(description = "Token 响应")
public class TokenVO {

    @Schema(description = "访问令牌" )
    private String token;

    @Schema(description = "过期时间(单位：秒)" )
    private Long expiresIn;

    public TokenVO(String token, Long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

}
