package top.xym.campusassistantapi.module.chat.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import top.xym.campusassistantapi.module.chat.dto.MultiModalQaRequest;
import top.xym.campusassistantapi.module.chat.dto.QaStreamResponse;

/**
 * AI 问答服务
 */
@Service
public class AIQnaService {

    private final ChatClient dashScopeChatClient;
    private final DashScopeChatOptions dashScopeChatOptions;
    private final ChatClient chatClient;
    private final ChatClient.Builder chatClientBuilder;

    // 静态代码块/初始化块：初始化 chatClientBuilder 初始化
    public AIQnaService(ChatClient dashScopeChatClient,
                        DashScopeChatOptions dashScopeChatOptions,
                        ChatClient.Builder chatClientBuilder) {
        this.dashScopeChatClient = dashScopeChatClient;
        this.dashScopeChatOptions = dashScopeChatOptions;
        this.chatClientBuilder = chatClientBuilder;
        this.chatClient = chatClientBuilder.build();
    }

    private static final String DEFAULT_QUESTION = """
            你是一个专业的编程导师，请回答学生的问题。
            学生问题：
            %s
            请给出详细且易懂的解答，并举例说明。
            """;

    /**
     * 普通问题
     */
    public String answerQuestionSimple(String question) {
        // formatted() 是Java字符串类的实例方法，用于将参数替换到字符串中的占位符位置。
        String prompt = DEFAULT_QUESTION.formatted(question);
        // 链式调用
        return dashScopeChatClient.prompt(prompt).call().content();
    }

    /**
     * 流式问题
     */
    public Flux<String> answerQuestionStream(String question) {
        String prompt = DEFAULT_QUESTION.formatted(question);
        return dashScopeChatClient.prompt(prompt).stream().content();
    }

    /**
     * 多模块流式问答
     */
    public Flux<QaStreamResponse> streamQa(MultiModalQaRequest request) {

        // 构建多模态 Prompt（融合文本+图片）
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("你是一个多模态AI助手，请根据以下提问内容提供准确、简洁的回答：\n");
        promptBuilder.append("文本提问：").append(request.getQuestion()).append("\n");

        // 拼接图片 URL (模型自动解析)
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            promptBuilder.append("图片参考地址：").append(String.join("、",request.getImageUrls())).append("\n");
        }

        promptBuilder.append("要求：回答分段落逐句返回，避免冗长，确保流式输出流畅。");
        String prompt = promptBuilder.toString();

        // 调用多模态模型（已开启multiModel=true）
        Flux<ChatResponse> chatResponseFlux = chatClient.prompt(prompt).stream().chatResponse();

        // 转换响应格式（标记图片解析内容）
        return chatResponseFlux
                .map(response -> {
                    String content = response.getResult().getOutput().getText();
                    // 给图片解析内容加标记，方便前端区分
                    if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                        return QaStreamResponse.content("[图片解析]" + content);
                    } else {
                        return QaStreamResponse.content(content);
                    }
                })
                .filter(qa -> qa.getContent() != null && !qa.getContent().trim().isEmpty()) // 过滤空内容
                .concatWith(Flux.just(QaStreamResponse.end())); // 结束标记
    }
}
