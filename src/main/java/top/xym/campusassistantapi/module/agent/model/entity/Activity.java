package top.xym.campusassistantapi.module.agent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动表实体类（适配MP规范）
 *
 * @author moqi
 */
@Data
@TableName("activity") // 对应数据库表名
public class Activity {

    /**
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动编号
     */
    private String activityId;

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动分类
     */
    private String category;

    /**
     * 活动时间
     */
    private LocalDateTime dateTime;

    /**
     * 活动地点
     */
    private String location;

    /**
     * 主办方
     */
    private String organizer;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 最大容量
     */
    private Integer capacity;

    /**
     * 已报名人数
     */
    private Integer registeredCount;

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