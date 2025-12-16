package top.xym.campusassistantapi.module.agent.model.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 座位预约表实体类（适配MP规范）
 *
 * @author moqi
 */
@Data
@TableName("seat_reservation") // 对应数据库表名
public class SeatReservation {

    /**
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 日期
     */
    private LocalDateTime date;

    /**
     * 时间段
     */
    private String timeSlot;

    /**
     * 总座位数
     */
    private Integer total;

    /**
     * 已预约
     */
    private Integer reserved;

    /**
     * 可用
     */
    private Integer available;

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