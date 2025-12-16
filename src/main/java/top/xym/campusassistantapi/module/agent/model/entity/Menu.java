package top.xym.campusassistantapi.module.agent.model.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜单表实体类（适配MP规范）
 *
 * @author moqi
 */
@Data
@TableName("menu") // 对应数据库表名
public class Menu {

    /**
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 食堂名称
     */
    private String diningHall;

    /**
     * 日期
     */
    private LocalDateTime date;

    /**
     * 菜品名称
     */
    private String itemName;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 类别
     */
    private String category;

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