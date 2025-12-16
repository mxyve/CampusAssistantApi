package top.xym.campusassistantapi.module.agent.model.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 食堂表实体类（适配MP规范）
 *
 * @author moqi
 */
@Data
@TableName("dining_hall") // 对应数据库表名
public class DiningHall {

    /**
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 食堂名称
     */
    private String name;

    /**
     * 校园区域
     */
    private String area;

    /**
     * 位置
     */
    private String location;

    /**
     * 营业时间
     */
    private String operatingHours;

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