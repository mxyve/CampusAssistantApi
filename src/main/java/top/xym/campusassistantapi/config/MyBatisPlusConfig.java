package top.xym.campusassistantapi.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * @author mqxu
 * @date 2025/12/12
 * @description MyBatis-Plus核心配置类，包含乐观锁、分页、自动填充、逻辑删除等核心配置
 **/
@Configuration
public class MyBatisPlusConfig {
    /**
     * 插件集合：乐观锁 + 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 1. 乐观锁插件（适配数据表中的version字段）
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 2. 分页插件（支持单表分页、联表分页）
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置数据库类型（MySQL）
        paginationInnerInterceptor.setDbType(com.baomidou.mybatisplus.annotation.DbType.MYSQL);
        // 溢出分页处理：默认返回最后一页
        paginationInnerInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }

    /**
     * 自动填充处理器（适配create_time、update_time字段）
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            /**
             * 插入操作时自动填充
             */
            @Override
            public void insertFill(MetaObject metaObject) {
                // 填充创建时间（兼容LocalDateTime和Date，根据实体类字段类型选择）
                strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                // 如果实体类用 Date 类型，替换为：
                // strictInsertFill(metaObject, "createTime", Date.class, new Date());
                // strictInsertFill(metaObject, "updateTime", Date.class, new Date());
            }

            /**
             * 更新操作时自动填充
             */
            @Override
            public void updateFill(MetaObject metaObject) {
                // 仅填充更新时间
                strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                // 如果实体类用 Date 类型，替换为：
                // strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
            }
        };
    }
}
