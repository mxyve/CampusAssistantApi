package top.xym.campusassistantapi.module.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "流式问答响应DTO")
public class QaStreamResponse {

    @Schema(description = "响应类型（CONTENT=内容片段，END=结束）")
    private String type;
    @Schema(description = "响应内容（CONTENT类型时为内容片段，END类型时为空）")
    private String content;

    // 静态工厂方法：内容片段
    public static QaStreamResponse content(String content) {
        return new QaStreamResponse("CONTENT", content);
    }

    // 静态工厂方法：流结束
    public static QaStreamResponse end() {
        return new QaStreamResponse("END", "");
    }

}
