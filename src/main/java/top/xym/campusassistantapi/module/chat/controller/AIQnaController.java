package top.xym.campusassistantapi.module.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import top.xym.campusassistantapi.module.chat.dto.QaRequest;
import top.xym.campusassistantapi.module.chat.dto.QaStreamResponse;
import top.xym.campusassistantapi.module.chat.service.AIQnaService;
import top.xym.starter.common.result.Result;

@RestController
@RequestMapping("/api/v1/qna")
@RequiredArgsConstructor
@Tag(name = "提问接口")
public class AIQnaController {

    private static final String DEFAULT_QUESTION = "你是谁？";

    private final AIQnaService aiQnaService;

    /**
     * 提问接口（普通响应流式）
     */
    @GetMapping("/ask")
    @Operation(summary = "普通问答接口", description = "返回 AI 问答内容")
    public Result<String> askSimple(@RequestParam(name = "question", required = false, defaultValue = DEFAULT_QUESTION) String question){
        return Result.success(aiQnaService.answerQuestionSimple(question));
    }

    /**
     * 提问接口（流式响应）
     */
    @GetMapping(value ="/ask/stream", produces = "text/plain;charset=UTF-8")
    @Operation(summary = "流式问答接口", description = "实时流式返回 AI 问答内容")
    public Flux<String> askStream(@RequestParam(name = "question", required = false, defaultValue = DEFAULT_QUESTION) String question){
        return aiQnaService.answerQuestionStream(question);
    }

    private final AIQnaService qaService;
    /**
     * 多模块流式问答接口
     */
    @PostMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Flux<QaStreamResponse> streamQa(
            @Valid @RequestBody QaRequest request,
            HttpServletResponse response) { // 注入响应对象
        response.setCharacterEncoding("UTF-8");
        return qaService.streamQa(request.getQuestion());
    }

}
