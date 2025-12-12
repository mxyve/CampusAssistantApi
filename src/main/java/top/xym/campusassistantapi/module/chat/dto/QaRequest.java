package top.xym.campusassistantapi.module.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "多模块问答请求DTO")
public class QaRequest {

    @NotBlank(message = "问题不能为空")
    @Schema(description = "提问内容（纯文本/语音转文字/OCR结果/文件内容）", example = "什么是Spring AI？")
    private String question;

}
