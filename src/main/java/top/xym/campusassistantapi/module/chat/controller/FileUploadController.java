package top.xym.campusassistantapi.module.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.xym.campusassistantapi.common.utils.OssUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/file")
@Tag(name = "图片上传接口")
public class FileUploadController {
    private final OssUtil ossUtil;

    public FileUploadController(OssUtil ossUtil) {
        this.ossUtil = ossUtil;
    }

    /**
     * 图片上传接口（前端传二进制，后端中转到OSS）
     */
    @PostMapping("/upload/image")
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        // 调用OSS工具类上传，返回临时URL
        String imageUrl = ossUtil.uploadImage(file);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("imageUrl", imageUrl);
        return result;
    }
}