package top.xym.campusassistantapi.module.message.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import top.xym.campusassistantapi.common.utils.SecurityUtils;
import top.xym.campusassistantapi.module.message.model.dto.MessageResponse;
import top.xym.campusassistantapi.module.message.model.dto.MessageSendRequest;
import top.xym.campusassistantapi.module.message.service.MessageService;

import java.util.List;

/**
 * 消息相关 HTTP 接口
 * 路径前缀：/api/messages
 */
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 流式发送消息接口
    @PostMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "流式发送消息")
    public Flux<String> sendMessageStream(
            @RequestBody MessageSendRequest request,
            HttpServletResponse response
    ) {
        response.setCharacterEncoding("UTF-8");

        Long userId = SecurityUtils.getCurrentUserId();

        return messageService.sendMessageStream(request, userId);
    }

    /**
     * 查询会话消息历史（核心修改：添加Token校验）
     */
    @GetMapping("/session/{sessionId}")
    @Operation(summary = "查询会话消息历史")
    public List<MessageResponse> getSessionMessages(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "20") Long size,
            @RequestHeader("Authorization") String authHeader // 新增：接收Authorization头
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        return messageService.getSessionMessages(sessionId, userId, current, size);
    }

}