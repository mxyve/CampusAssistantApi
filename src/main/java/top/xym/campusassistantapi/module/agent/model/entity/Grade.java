package top.xym.campusassistantapi.module.agent.model.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 成绩表实体类（适配MP规范）
 *
 * @author moqi
 */
@Data
@TableName("grade") // 对应数据库表名
public class Grade {

    /**
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生编号
     */
    private String studentId;

    /**
     * 学期(2025-1=秋季/2025-2=冬季)
     */
    private String semester;

    /**
     * 课程编号
     */
    private String courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 学分
     */
    private Integer credits;

    /**
     * 等级
     */
    private String grade;

    /**
     * 乐观锁版本号（MP乐观锁插件使用）
     */
    @Version
    private Integer version;

    /**
     * 逻辑删除字段（MP逻辑删除插件使用，0=未删，1=已删）
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间（MP自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间（MP自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}