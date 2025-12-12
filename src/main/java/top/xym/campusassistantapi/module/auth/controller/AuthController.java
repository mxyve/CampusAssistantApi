package top.xym.campusassistantapi.module.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.xym.campusassistantapi.common.utils.SecurityUtils;
import top.xym.campusassistantapi.module.auth.model.dto.LoginDTO;
import top.xym.campusassistantapi.module.auth.model.dto.SmsLoginDTO;
import top.xym.campusassistantapi.module.auth.model.vo.TokenVO;
import top.xym.campusassistantapi.module.auth.service.AuthService;
import top.xym.starter.common.result.Result;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "登录、登出等认证相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sessions")
    @Operation(summary = "账号密码登录")
    public Result<TokenVO> login(@Validated @RequestBody LoginDTO dto) {
        TokenVO token = authService.login(dto);
        return Result.success(token);
    }

    @PostMapping("/sms-codes")
    @Operation(summary = "发送短信验证码")
    public Result<String> sendSmsCode(
            @Parameter(description = "手机号")
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
            @RequestParam String mobile) {
        authService.sendSmsCode(mobile);
        return Result.success();
    }

    @PostMapping("/sessions/sms")
    @Operation(summary = "短信验证码登录")
    public Result<TokenVO> smsLogin(@Validated @RequestBody SmsLoginDTO dto) {
        TokenVO token = authService.smsLogin(dto);
        return Result.success(token);
    }

    @DeleteMapping("/sessions/current")
    @Operation(summary = "登出")
    public Result<Void> logout() {
        Long userId = SecurityUtils.getCurrentUserId();
        authService.logoutByUserId(userId);
        return Result.success();
    }

}
