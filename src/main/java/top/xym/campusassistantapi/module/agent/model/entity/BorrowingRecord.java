package top.xym.campusassistantapi.module.agent.model.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 借阅记录表实体类（适配MP规范）
 *
 * @author moqi
 */
@Data
@TableName("borrowing_record") // 对应数据库表名
public class BorrowingRecord {

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
     * ISBN编号
     */
    private String isbn;

    /**
     * 书名
     */
    private String bookTitle;

    /**
     * 借阅日期
     */
    private LocalDateTime borrowDate;

    /**
     * 到期日期
     */
    private LocalDateTime dueDate;

    /**
     * 状态(Borrowed/Returned)
     */
    private String status;

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