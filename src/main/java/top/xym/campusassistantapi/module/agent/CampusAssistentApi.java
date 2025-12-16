package top.xym.campusassistantapi.module.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import top.xym.campusassistantapi.module.agent.tools.*;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 智慧校园助手配置类
 *
 * @author moqi
 */
@Configuration
public class CampusAssistentApi {

    private static final String INSTRUCTION = """
            你是一名乐于助人的智慧校园助手，专为在校学生提供服务。你的职责是协助学生处理各类校园相关事务并解答他们的问题。
            
            你可以使用以下工具和功能：
            
            1. 课程查询工具 - 帮助学生查询课程表、课程详情和选课信息
            2. 成绩查询工具 - 帮助学生查询成绩、学期成绩和成绩统计信息
            3. 校园导航工具 - 帮助学生查找位置、获取导航路线和楼宇信息
            4. 活动通知工具 - 帮助学生查询校园活动、赛事并检查报名状态
            5. 图书馆服务工具 - 帮助学生搜索图书、查询借阅记录和座位可用情况
            6. 食堂信息工具 - 帮助学生查询食堂菜单、营业时间和位置信息
            7. 校园知识工具 - 解答校园规章制度、政策和常见问题
            
            操作准则：
            - 始终保持友好、乐于助人且专业的态度
            - 学生提问时，使用合适的工具获取准确信息
            - 如果学生询问的信息你没有相关数据，礼貌地说明无法提供该信息
            - 对于需要学生ID的查询，如果未提供，主动向学生索要
            - 提供清晰、简洁的回答
            - 如果多个工具都适用，选择最合适的一个
            
            记住：你的存在是为了让学生的校园生活更便捷！
            """;

    @Bean
    public ReactAgent smartCampusReactAgent(
            ChatModel chatModel,
            ToolCallback courseQueryTool,
            ToolCallback gradeQueryTool,
            ToolCallback campusNavigationTool,
            ToolCallback activityNotificationTool,
            ToolCallback libraryServiceTool,
            ToolCallback diningInfoTool,
            ToolCallback campusKnowledgeTool) {
        return ReactAgent.builder()
                .name("CampusAssistantApi")
                .model(chatModel)
                .instruction(INSTRUCTION)
                .enableLogging(true)
                .tools(
                        courseQueryTool,
                        gradeQueryTool,
                        campusNavigationTool,
                        activityNotificationTool,
                        libraryServiceTool,
                        diningInfoTool,
                        campusKnowledgeTool
                )
                .build();
    }

    // 课程查询工具
    @Bean
    public ToolCallback courseQueryTool() {
        return FunctionToolCallback.builder("course_query", new CourseQueryTool())
                .description(CourseQueryTool.DESCRIPTION)
                .inputType(CourseQueryTool.CourseQueryRequest.class)
                .build();
    }

    // 成绩查询工具
    @Bean
    public ToolCallback gradeQueryTool() {
        return FunctionToolCallback.builder("grade_query", new GradeQueryTool())
                .description(GradeQueryTool.DESCRIPTION)
                .inputType(GradeQueryTool.GradeQueryRequest.class)
                .build();
    }

    // 校园导航工具
    @Bean
    public ToolCallback campusNavigationTool() {
        return FunctionToolCallback.builder("campus_navigation", new CampusNavigationTool())
                .description(CampusNavigationTool.DESCRIPTION)
                .inputType(CampusNavigationTool.NavigationRequest.class)
                .build();
    }

    // 活动通知工具
    @Bean
    public ToolCallback activityNotificationTool() {
        return FunctionToolCallback.builder("activity_notification", new ActivityNotificationTool())
                .description(ActivityNotificationTool.DESCRIPTION)
                .inputType(ActivityNotificationTool.ActivityRequest.class)
                .build();
    }

    // 图书馆服务工具
    @Bean
    public ToolCallback libraryServiceTool() {
        return FunctionToolCallback.builder("library_service", new LibraryServiceTool())
                .description(LibraryServiceTool.DESCRIPTION)
                .inputType(LibraryServiceTool.LibraryRequest.class)
                .build();
    }

    // 食堂信息工具
    @Bean
    public ToolCallback diningInfoTool() {
        return FunctionToolCallback.builder("dining_info", new DiningInfoTool())
                .description(DiningInfoTool.DESCRIPTION)
                .inputType(DiningInfoTool.DiningRequest.class)
                .build();
    }

    // 校园知识工具
    @Bean
    public ToolCallback campusKnowledgeTool() {
        return FunctionToolCallback.builder("campus_knowledge", new CampusKnowledgeTool())
                .description(CampusKnowledgeTool.DESCRIPTION)
                .inputType(CampusKnowledgeTool.KnowledgeRequest.class)
                .build();
    }
}