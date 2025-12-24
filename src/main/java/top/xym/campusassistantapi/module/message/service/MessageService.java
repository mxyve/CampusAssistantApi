package top.xym.campusassistantapi.module.message.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import top.xym.campusassistantapi.module.message.mapper.ChatMessageMapper;
import top.xym.campusassistantapi.module.message.model.dto.MessageResponse;
import top.xym.campusassistantapi.module.message.model.dto.MessageSendRequest;
import top.xym.campusassistantapi.module.message.model.entity.ChatMessage;
import top.xym.campusassistantapi.module.session.model.dto.SessionResponse;
import top.xym.campusassistantapi.module.session.model.dto.SessionUpdateTitleRequest;
import top.xym.campusassistantapi.module.session.service.SessionService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 消息服务：处理用户消息发送、AI流式响应、消息存储与查询
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChatMessageMapper chatMessageMapper;
    private final SessionService sessionService;
    private final ChatModel chatModel;

    /**
     * 发送消息并获取AI流式响应（核心方法）
     * 特性：片段合并、完整内容保存、会话最后消息更新、异常处理
     */
    public Flux<String> sendMessageStream(MessageSendRequest request, Long userId) {
        AtomicLong assistantMessageId = new AtomicLong(0);
        AtomicReference<StringBuilder> fullContentRef = new AtomicReference<>(new StringBuilder());

        try {
            validateSession(request.getSessionId(), userId);
            saveUserMessage(request, userId);
            Prompt prompt = buildPrompt(request.getContent());
            ChatMessage assistantMessage = saveAssistantMessageTemp(request, userId);
            assistantMessageId.set(assistantMessage.getId());

            return chatModel.stream(prompt)
                    .mapNotNull(chatResponse -> {
                        String content = chatResponse.getResult().getOutput().getText();
                        return (content == null || content.trim().isEmpty()) ? null : content;
                    })
                    .bufferTimeout(20, java.time.Duration.ofMillis(300))
                    .map(fragments -> String.join("", fragments))
                    .filter(mergedContent -> !mergedContent.isEmpty())
                    // 步骤3：累积片段到完整内容（用于最终保存）
                    .doOnNext(mergedContent -> {
                        fullContentRef.get().append(mergedContent);
                    })
                    // 删除中间片段更新会话的逻辑！！！
                    // 之前这里会每次合并片段后更新lastMessage，导致最终只保留最后一个片段
                    // 关键修改：移除此处的 sessionService.updateLastMessage 调用
                    // 步骤5：流式响应完成后，更新助手消息完整内容 + 会话最后消息（完整内容）
                    .doOnComplete(() -> {
                        String fullText = fullContentRef.get().toString();
                        autoGenerateSessionTitle(request.getSessionId(), userId, fullText);
                        // 新增：流式结束后，用完整内容更新会话lastMessage
                        updateSessionLastMessage(request.getSessionId(), fullText);
                        updateAssistantMessageFullContent(assistantMessageId.get(), fullText, "completed");
                    })
                    .doOnError(error -> {
                        String errorText = "AI响应失败：" + error.getMessage();
                        fullContentRef.get().append(errorText);
                        // 异常时也用完整错误信息更新会话lastMessage
                        updateSessionLastMessage(request.getSessionId(), errorText);
                        updateAssistantMessageFullContent(assistantMessageId.get(), errorText, "failed");
                    });

        } catch (Exception e) {
            return Flux.error(new RuntimeException("流式请求初始化失败：" + e.getMessage()));
        }
    }

    private void updateSessionLastMessage(Long sessionId, String fullContent) {
        try {
            sessionService.updateLastMessage(sessionId, fullContent);
        } catch (Exception e) {
            // 打印日志（不影响核心流程）
            System.err.println("更新会话最后消息失败：sessionId=" + sessionId + ", error=" + e.getMessage());
        }
    }

    /**
     * 分页查询会话内的消息历史（按创建时间升序）
     * @param sessionId 会话ID
     * @param userId 用户ID（权限校验）
     * @param current 页码（从1开始）
     * @param size 每页条数
     * @return 消息历史分页结果
     */
    public List<MessageResponse> getSessionMessages(Long sessionId, Long userId, Long current, Long size) {
        // 1. 验证会话合法性
        validateSession(sessionId, userId);

        // 2. 构建查询条件：会话ID+未删除+按创建时间升序
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getSessionId, sessionId)
                .eq(ChatMessage::getDeleted, 0)
                .orderByAsc(ChatMessage::getCreateTime);

        // 3. 执行分页查询（MyBatis-Plus语法）
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChatMessage> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChatMessage> messagePage = chatMessageMapper.selectPage(page, queryWrapper);

        // 4. 转换为响应DTO
        return messagePage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 验证会话合法性（用户是否有权访问该会话）
     */
    private void validateSession(Long sessionId, Long userId) {
        try {
            sessionService.getSession(sessionId, userId);
        } catch (RuntimeException e) {
            throw new RuntimeException("会话不存在或无权访问：" + e.getMessage());
        }
    }

    /**
     * 构建AI请求提示词（系统消息+用户消息）
     */
    private Prompt buildPrompt(String userContent) {
        List<Message> messages = new ArrayList<>();
        // 系统消息：定义AI助手的角色和行为
        messages.add(new SystemMessage("你是一个专业、友好的智能助手，能够准确、清晰地回答用户的问题。" +
                "回答需结构清晰，语言流畅，避免使用零散的短句。"));
        // 用户消息：用户的实际查询内容
        messages.add(new UserMessage(userContent));
        return new Prompt(messages);
    }

    /**
     * 保存用户消息到数据库
     */
    private void saveUserMessage(MessageSendRequest request, Long userId) {
        ChatMessage userMessage = new ChatMessage();
        userMessage.setSessionId(request.getSessionId());
        userMessage.setUserId(userId);
        userMessage.setRole("user"); // 角色：用户
        userMessage.setContent(request.getContent());
        userMessage.setModelName(request.getModelName());
        userMessage.setStatus(1); // 状态：1-成功
        userMessage.setCreateTime(LocalDateTime.now());
        userMessage.setUpdateTime(LocalDateTime.now());
        userMessage.setDeleted(0); // 未删除

        chatMessageMapper.insert(userMessage);
    }

    /**
     * 临时保存助手消息（初始状态：处理中）
     */
    private ChatMessage saveAssistantMessageTemp(MessageSendRequest request, Long userId) {
        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setSessionId(request.getSessionId());
        assistantMessage.setUserId(userId);
        assistantMessage.setRole("assistant"); // 角色：AI助手
        assistantMessage.setContent(""); // 初始内容为空，后续更新
        assistantMessage.setModelName(request.getModelName());
        assistantMessage.setStatus(0); // 状态：0-处理中
        assistantMessage.setCreateTime(LocalDateTime.now());
        assistantMessage.setUpdateTime(LocalDateTime.now());
        assistantMessage.setDeleted(0); // 未删除

        chatMessageMapper.insert(assistantMessage);
        return assistantMessage;
    }

    /**
     * 流式响应结束后，更新助手消息的完整内容和状态
     */
    private void updateAssistantMessageFullContent(Long messageId, String fullContent, String status) {
        if (messageId == 0) {
            return;
        }

        // 构建更新对象
        ChatMessage updateMsg = new ChatMessage();
        updateMsg.setId(messageId);
        updateMsg.setContent(fullContent); // 保存完整内容
        updateMsg.setStatus("completed".equals(status) ? 1 : 2); // 1-成功，2-失败
        updateMsg.setUpdateTime(LocalDateTime.now());

        // 构建更新条件：消息ID+角色为助手+未删除
        LambdaQueryWrapper<ChatMessage> updateWrapper = new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getId, messageId)
                .eq(ChatMessage::getRole, "assistant")
                .eq(ChatMessage::getDeleted, 0);

        int rows = chatMessageMapper.update(updateMsg, updateWrapper);
        if (rows == 0) {
            throw new RuntimeException("助手消息更新失败");
        }
    }

    /**
     * 自动生成会话标题（如果用户未指定标题）
     */
    private void autoGenerateSessionTitle(Long sessionId, Long userId, String fullText) {
        try {
            // 1. 查询当前会话信息
            SessionResponse session = sessionService.getSession(sessionId, userId);
            // 2. 判断是否需要自动生成标题（标题为空、默认值或过短）
            if (session.getTitle() == null || session.getTitle().trim().isEmpty()
                    || session.getTitle().equals("新会话") || session.getTitle().length() < 5) {
                // 3. 提取AI响应前20字作为标题（末尾加省略号）
                String autoTitle = fullText.length() > 20 ? fullText.substring(0, 20) + "..." : fullText;
                // 4. 更新会话标题：用 Setter 方法赋值（无需构造函数）
                SessionUpdateTitleRequest titleRequest = new SessionUpdateTitleRequest();
                titleRequest.setTitle(autoTitle); // 直接调用 setTitle 方法
                sessionService.updateSessionTitle(sessionId, userId, titleRequest);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 将ChatMessage实体转换为前端响应DTO
     */
    private MessageResponse convertToResponse(ChatMessage chatMessage) {
        MessageResponse response = new MessageResponse(
                chatMessage.getId(),
                chatMessage.getSessionId(),
                chatMessage.getUserId(),
                chatMessage.getRole(),
                chatMessage.getContent(),
                chatMessage.getModelName(),
                null, // tokens：ChatMessage 无该字段，设为 null
                0,    // hasThinking：默认 0（未开启）
                null, // thinkingContent：默认 null
                0,    // webSearch：默认 0（未开启）
                chatMessage.getStatus(), // 消息状态
                "",   // statusDesc：先传空字符串，后续通过方法设置
                chatMessage.getCreateTime(),
                chatMessage.getUpdateTime()
        );
        // 调用状态转换方法，设置 statusDesc
        response.setStatusDesc(chatMessage.getStatus());
        return response;
    }

}