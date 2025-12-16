package top.xym.campusassistantapi.module.agent.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.chat.model.ToolContext;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * 用于查询校园活动、赛事和通知的工具类。
 *
 * @author moqi
 */
public class ActivityNotificationTool implements BiFunction<ActivityNotificationTool.ActivityRequest, ToolContext, String> {

    public static final String DESCRIPTION = """
            查询校园活动、赛事和通知信息，包括讲座、社团活动、公告等。
            
            支持的操作：
            - queryActivityList: 查询活动列表（可按分类筛选）
            - queryActivityDetail: 查询指定活动的详细信息
            - queryRegistration: 查询活动报名状态
            
            使用示例：
            - 查询活动列表：operation="queryActivityList", category="讲座"
            - 查询活动详情：operation="queryActivityDetail", activityId="ACT001"
            - 查询报名状态：operation="queryRegistration", activityId="ACT001", studentId="2022001"
            """;

    // 模拟数据：活动数据库
    private static final Map<String, Activity> ACTIVITIES = new HashMap<>();
    // 模拟数据：报名信息
    private static final Map<String, Set<String>> REGISTRATIONS = new HashMap<>();

    static {
        // 初始化模拟活动数据
        ACTIVITIES.put("ACT001", new Activity("ACT001", "人工智能前沿讲座", "讲座", "2025-09-15 14:00",
                "教学楼A 201", "计算机学院", "邀请业界专家分享AI最新进展", 100, 45));
        ACTIVITIES.put("ACT002", new Activity("ACT002", "秋季运动会", "体育", "2025-10-10 08:00",
                "体育馆", "体育部", "全校师生参与的秋季运动会", 500, 320));
        ACTIVITIES.put("ACT003", new Activity("ACT003", "编程竞赛", "竞赛", "2025-09-20 09:00",
                "实验楼 301", "计算机学院", "ACM编程竞赛校内选拔", 50, 28));
        ACTIVITIES.put("ACT004", new Activity("ACT004", "音乐节", "文艺", "2025-10-05 19:00",
                "学生活动中心", "学生会", "校园音乐节，多支乐队演出", 300, 180));
        ACTIVITIES.put("ACT005", new Activity("ACT005", "创业分享会", "讲座", "2025-09-25 15:00",
                "教学楼B 101", "创业学院", "成功创业者经验分享", 80, 52));

        // 初始化模拟报名数据
        REGISTRATIONS.put("ACT001", new HashSet<>(Arrays.asList("2022001", "2022002", "2022003")));
        REGISTRATIONS.put("ACT002", new HashSet<>(Arrays.asList("2022001", "2022002")));
        REGISTRATIONS.put("ACT003", new HashSet<>(Arrays.asList("2022002")));
    }

    @Override
    public String apply(ActivityRequest request, ToolContext toolContext) {
        if (request.operation == null || request.operation.trim().isEmpty()) {
            return "错误：必须指定operation参数";
        }

        try {
            return switch (request.operation.toLowerCase()) {
                case "queryactivitylist" -> queryActivityList(request.category, request.date);
                case "queryactivitydetail" -> queryActivityDetail(request.activityId);
                case "queryregistration" -> queryRegistration(request.activityId, request.studentId);
                default ->
                        "错误：未知操作。支持的操作：queryActivityList（查询活动列表）、queryActivityDetail（查询活动详情）、queryRegistration（查询报名状态）";
            };
        } catch (Exception e) {
            return "错误：" + e.getMessage();
        }
    }

    /**
     * 查询活动列表（支持分类和日期筛选）
     *
     * @param category 活动分类
     * @param date     日期筛选（格式：YYYY-MM-DD）
     * @return 格式化的活动列表信息
     */
    private String queryActivityList(String category, String date) {
        List<Activity> activities = new ArrayList<>(ACTIVITIES.values());

        // 按分类筛选
        if (category != null && !category.trim().isEmpty()) {
            activities = activities.stream()
                    .filter(a -> a.category.equals(category))
                    .collect(Collectors.toList());
        }

        // 按日期筛选
        if (date != null && !date.trim().isEmpty()) {
            activities = activities.stream()
                    .filter(a -> a.dateTime.startsWith(date))
                    .collect(Collectors.toList());
        }

        if (activities.isEmpty()) {
            return "未找到相关活动" +
                    (category != null ? " [分类：" + category + "]" : "") +
                    (date != null ? " [日期：" + date + "]" : "");
        }

        StringBuilder result = new StringBuilder();
        result.append("校园活动列表：\n\n");
        for (Activity activity : activities) {
            result.append(String.format("- %s [%s]\n", activity.title, activity.activityId));
            result.append(String.format("  分类：%s | 时间：%s | 地点：%s\n", activity.category, activity.dateTime, activity.location));
            result.append(String.format("  报名人数：%d/%d\n\n", activity.registeredCount, activity.capacity));
        }
        return result.toString();
    }

    /**
     * 查询指定活动的详细信息
     *
     * @param activityId 活动ID
     * @return 格式化的活动详情
     */
    private String queryActivityDetail(String activityId) {
        if (activityId == null || activityId.trim().isEmpty()) {
            return "错误：查询活动详情操作必须指定activityId参数";
        }

        Activity activity = ACTIVITIES.get(activityId.toUpperCase());
        if (activity == null) {
            return "错误：未找到活动：" + activityId;
        }

        int availableSpots = activity.capacity - activity.registeredCount;

        return String.format("""
                        活动详情：
                        活动ID：%s
                        标题：%s
                        分类：%s
                        时间：%s
                        地点：%s
                        主办方：%s
                        描述：%s
                        最大容量：%d
                        已报名人数：%d
                        剩余名额：%d""",
                activity.activityId, activity.title, activity.category, activity.dateTime,
                activity.location, activity.organizer, activity.description,
                activity.capacity, activity.registeredCount, availableSpots);
    }

    /**
     * 查询学生的活动报名状态
     *
     * @param activityId 活动ID
     * @param studentId  学生ID
     * @return 格式化的报名状态信息
     */
    private String queryRegistration(String activityId, String studentId) {
        if (activityId == null || activityId.trim().isEmpty()) {
            return "错误：查询报名状态操作必须指定activityId参数";
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            return "错误：查询报名状态操作必须指定studentId参数";
        }

        Activity activity = ACTIVITIES.get(activityId.toUpperCase());
        if (activity == null) {
            return "错误：未找到活动：" + activityId;
        }

        Set<String> registrations = REGISTRATIONS.getOrDefault(activityId.toUpperCase(), new HashSet<>());
        boolean isRegistered = registrations.contains(studentId);

        if (isRegistered) {
            return String.format("""
                            报名状态：
                            学生%s已报名活动%s（%s）
                            活动时间：%s
                            活动地点：%s""",
                    studentId, activityId, activity.title, activity.dateTime, activity.location);
        } else {
            int availableSpots = activity.capacity - activity.registeredCount;
            return String.format("""
                            报名状态：
                            学生%s未报名活动%s（%s）
                            剩余名额：%d/%d""",
                    studentId, activityId, activity.title, availableSpots, activity.capacity);
        }
    }

    /**
     * 活动查询请求参数类
     */
    public static class ActivityRequest {
        @JsonProperty(required = true)
        @JsonPropertyDescription("要执行的操作：queryActivityList（查询活动列表）、queryActivityDetail（查询活动详情）、queryRegistration（查询报名状态）")
        public String operation;

        @JsonPropertyDescription("活动分类（例如：讲座、体育、竞赛、文艺）")
        public String category;

        @JsonPropertyDescription("日期筛选（格式：YYYY-MM-DD）")
        public String date;

        @JsonPropertyDescription("活动ID（查询活动详情和报名状态时必填）")
        public String activityId;

        @JsonPropertyDescription("学生ID（查询报名状态时必填）")
        public String studentId;
    }

    /**
     * 活动实体类
     */
    private static class Activity {
        String activityId;      // 活动ID
        String title;           // 活动标题
        String category;        // 活动分类
        String dateTime;        // 活动时间
        String location;        // 活动地点
        String organizer;       // 主办方
        String description;     // 活动描述
        int capacity;           // 最大容量
        int registeredCount;    // 已报名人数

        Activity(String activityId, String title, String category, String dateTime,
                 String location, String organizer, String description, int capacity, int registeredCount) {
            this.activityId = activityId;
            this.title = title;
            this.category = category;
            this.dateTime = dateTime;
            this.location = location;
            this.organizer = organizer;
            this.description = description;
            this.capacity = capacity;
            this.registeredCount = registeredCount;
        }
    }
}