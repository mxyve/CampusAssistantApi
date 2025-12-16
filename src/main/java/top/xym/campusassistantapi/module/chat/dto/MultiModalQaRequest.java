package top.xym.campusassistantapi.module.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "多模态流式问答请求DTO（文本+图片）")
public class MultiModalQaRequest {

    @NotBlank(message = "提问内容不能为空")
    @Schema(description = "文本问题")
    private String question;

    @Schema(description = "OSS临时图片URL列表（前端从上传接口获取）")
    private List<String> imageUrls;

}
