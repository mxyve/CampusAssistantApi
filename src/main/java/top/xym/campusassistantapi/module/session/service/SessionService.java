package top.xym.campusassistantapi.module.session.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.xym.campusassistantapi.common.model.dto.PageResponse;
import top.xym.campusassistantapi.module.message.mapper.ChatMessageMapper;
import top.xym.campusassistantapi.module.message.model.entity.ChatMessage;
import top.xym.campusassistantapi.module.message.service.MessageService;
import top.xym.campusassistantapi.module.session.model.dto.SessionUpdateTitleRequest;
import top.xym.campusassistantapi.module.session.mapper.ChatSessionMapper;
import top.xym.campusassistantapi.module.session.model.dto.SessionCreateRequest;
import top.xym.campusassistantapi.module.session.model.dto.SessionResponse;
import top.xym.campusassistantapi.module.session.model.entity.ChatSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    @Autowired
    private ChatSessionMapper sessionMapper;
    @Autowired
    private ChatMessageMapper messageMapper;

    /**
     * 创建会话
     */
    public SessionResponse createSession(SessionCreateRequest request, Long userId) {
        ChatSession session = new ChatSession();
        session.setTitle(request.getTitle());
        session.setUserId(userId);
        session.setModelName(request.getModelName());
        session.setStatus(0);
        session.setCreateTime(LocalDateTime.now()); // 补充创建时间
        session.setUpdateTime(LocalDateTime.now()); // 补充更新时间
        session.setDeleted(0);

        sessionMapper.insert(session);
        return convertToResponse(session);
    }

    private SessionResponse convertToResponse(ChatSession session) {
        return new SessionResponse(
                session.getId(),
                session.getTitle(),
                session.getModelName(),
                session.getStatus(),
                session.getLastMessage(),
                session.getLastMessageTime(),
                session.getCreateTime(),
                session.getUpdateTime()
        );
    }

    /**
     * 分页获取用户所有会话
     */
    public PageResponse<SessionResponse> getUserSessions(Long userId, Long current, Long size) {
        Page<ChatSession> page = new Page<>(current, size);
        LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getDeleted, 0)
                .orderByDesc(ChatSession::getCreateTime);
        Page<ChatSession> sessionPage = sessionMapper.selectPage(page, queryWrapper);
        List<SessionResponse> records = sessionPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                sessionPage.getCurrent(),
                sessionPage.getSize(),
                sessionPage.getTotal(),
                sessionPage.getPages(),
                records
        );
    }


    public SessionResponse getSession(Long sessionId, Long userId) {
        ChatSession session = sessionMapper.selectOne(
                new LambdaQueryWrapper<ChatSession>()
                        .eq(ChatSession::getId, sessionId)
                        .eq(ChatSession::getUserId, userId)
                        .eq(ChatSession::getDeleted, 0)
        );
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }
        return convertToResponse(session);
    }

    public SessionResponse updateSessionTitle(Long sessionId, Long userId, SessionUpdateTitleRequest request) {
        ChatSession session = sessionMapper.selectOne(
                new LambdaQueryWrapper<ChatSession>()
                        .eq(ChatSession::getId, sessionId)
                        .eq(ChatSession::getUserId, userId)
                        .eq(ChatSession::getDeleted, 0)
        );
        if (session == null) {
            throw new RuntimeException("会话不存在或已删除");
        }
        session.setTitle(request.getTitle());
        session.setUpdateTime(LocalDateTime.now());
        int rows = sessionMapper.updateById(session);
        if (rows == 0) {
            throw new RuntimeException("会话标题修改失败");
        }
        return convertToResponse(session);
    }

    public void updateLastMessage(Long sessionId, String mergedLastMessage) {
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session == null || session.getDeleted() == 1) {
            throw new RuntimeException("会话不存在或已删除");
        }
        ChatSession updateSession = new ChatSession();
        updateSession.setId(sessionId);
        updateSession.setLastMessage(mergedLastMessage);
        updateSession.setLastMessageTime(LocalDateTime.now());
        updateSession.setUpdateTime(LocalDateTime.now());
        LambdaQueryWrapper<ChatSession> updateWrapper = new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getId, sessionId)
                .eq(ChatSession::getDeleted, 0);
        int rows = sessionMapper.update(updateSession, updateWrapper);
        if (rows == 0) {
            throw new RuntimeException("会话最后消息更新失败");
        }
    }

    // 删除会话
    public void deleteSession(Long sessionId, Long userId) {
        // 检查会话是否存在且属于当前用户
        ChatSession session = sessionMapper.selectOne(
                new LambdaQueryWrapper<ChatSession>()
                        .eq(ChatSession::getId, sessionId)
                        .eq(ChatSession::getUserId, userId)
        );
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }

        // 先删除关联的 chat_message
        LambdaQueryWrapper<ChatMessage> messageWrapper = new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getSessionId, sessionId);
        messageMapper.delete(messageWrapper);

        // 再删除会话
        sessionMapper.deleteById(sessionId);
    }

}
