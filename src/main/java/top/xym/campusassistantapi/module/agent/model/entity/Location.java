package top.xym.campusassistantapi.module.agent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("location") // 对应数据库表名
public class Location {

    /**
     * 主键ID（自增，对应MP全局配置的id-type: auto）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 位置名称
     */
    private String name;

    /**
     * 建筑名称（如A栋、B栋）
     */
    private String building;

    /**
     * 校园区域（如北校区、南校区）
     */
    private String area;

    /**
     * 描述
     */
    private String description;

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
    @TableField(fill = FieldFill.INSERT) // 仅插入时填充
    private LocalDateTime createTime;

    /**
     * 更新时间（MP自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入+更新时填充
    private LocalDateTime updateTime;
}