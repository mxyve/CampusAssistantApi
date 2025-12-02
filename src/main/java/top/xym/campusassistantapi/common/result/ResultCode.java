package top.xym.campusassistantapi.common.result;

import lombok.Getter;

/**
 * 响应状态码
 */
@Getter
public enum ResultCode {
    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 操作失败
     */
    FAIL(400, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    UNAUTHORIZED(401, "未登录"),

    FORBIDDEN(403, "没有权限"),

    NOT_FOUND(404, "资源不存在"),

    SYSTEM_ERROR(500, "系统异常");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
