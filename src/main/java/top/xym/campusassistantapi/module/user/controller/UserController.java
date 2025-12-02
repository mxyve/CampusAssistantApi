package top.xym.campusassistantapi.module.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.xym.campusassistantapi.module.user.model.dto.UserDTO;
import top.xym.campusassistantapi.module.user.model.dto.UserEditDTO;
import top.xym.campusassistantapi.module.user.model.vo.UserVO;
import top.xym.campusassistantapi.module.user.service.UserService;
import top.xym.starter.common.result.Result;
import top.xym.campusassistantapi.common.utils.SecurityUtils;
/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {

    private final UserService userService;


    @GetMapping("/{id}")
    @Operation(summary = "根据用户 ID 获取用户信息")
    public Result<UserVO> getById(@Parameter(description = "用户 ID") @PathVariable Long id) {
        UserVO user = userService.getById(id);
        return Result.success(user);
    }

    @GetMapping("/user-info")
    @Operation(summary = "获取当前登录用户信息")
    public Result<UserVO> getUserInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserVO userInfo = userService.getById(userId);
        return Result.success(userInfo);
    }

    @PutMapping("/update-profile")
    @Operation(summary = "修改个人信息")
    public Result<String> updateProfile(@Validated @RequestBody UserEditDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updateProfile(dto, userId);
        return Result.success("修改成功");
    }


}
