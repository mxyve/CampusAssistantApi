package top.xym.campusassistantapi.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssUtil {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String tempDir;
    private Integer urlExpire;

    /**
     * 上传图片到OSS，返回临时访问URL
     */
    public String uploadImage(MultipartFile file) throws Exception {
        // 1. 生成唯一文件名（避免重复）
        String originalFilename = file.getOriginalFilename();
        String fileName = tempDir + UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

        // 2. 上传文件到OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, file.getInputStream());

        // 3. 生成临时URL（带过期时间）
        Date expireDate = new Date(System.currentTimeMillis() + urlExpire * 1000L);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileName);
        request.setExpiration(expireDate);
        URL presignedUrl = ossClient.generatePresignedUrl(request);

        ossClient.shutdown();
        // 返回临时URL（前端仅能在有效期内访问）
        return presignedUrl.toString();
    }
}
