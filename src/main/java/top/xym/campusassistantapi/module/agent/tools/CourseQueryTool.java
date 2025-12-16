package top.xym.campusassistantapi.module.agent.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.chat.model.ToolContext;

import java.util.*;
import java.util.function.BiFunction;

/**
 * 用于查询课程信息、日程安排和选课情况的工具类。
 *
 * @author moqi
 */
public class CourseQueryTool implements BiFunction<CourseQueryTool.CourseQueryRequest, ToolContext, String> {

    public static final String DESCRIPTION = """
            查询课程信息，包括课程表、课程详情和选课状态。
            
            支持的操作：
            - querySchedule: 查询指定学生或学期的课程表
            - queryCourseDetail: 查询指定课程的详细信息
            - queryEnrollment: 查看选课状态和可用课程
            
            使用示例：
            - 查询课程表：operation="querySchedule", studentId="2022001", semester="2025-1"
            - 查询课程详情：operation="queryCourseDetail", courseId="CS101"
            - 查询选课信息：operation="queryEnrollment", semester="2025-1"
            """;

    // 模拟数据：课程数据库
    private static final Map<String, Course> COURSES = new HashMap<>();
    // 模拟数据：学生课程表
    private static final Map<String, List<Schedule>> STUDENT_SCHEDULES = new HashMap<>();

    static {
        // 初始化模拟课程数据
        COURSES.put("CS101", new Course("CS101", "计算机科学导论", "计算机学院", 3, 60, "周一 8:00-10:00", "A101"));
        COURSES.put("CS201", new Course("CS201", "数据结构", "计算机学院", 4, 50, "周三 14:00-16:00", "A201"));
        COURSES.put("MATH101", new Course("MATH101", "高等数学", "数学学院", 4, 80, "周二 10:00-12:00", "B101"));
        COURSES.put("ENG101", new Course("ENG101", "大学英语", "外语学院", 2, 60, "周四 14:00-16:00", "C101"));
        COURSES.put("PHY101", new Course("PHY101", "大学物理", "物理学院", 3, 70, "周五 8:00-10:00", "D101"));

        // 初始化模拟课程表数据
        STUDENT_SCHEDULES.put("2022001", Arrays.asList(
                new Schedule("2025-1", "CS101", "计算机科学导论", "周一 8:00-10:00", "A101"),
                new Schedule("2025-1", "MATH101", "高等数学", "周二 10:00-12:00", "B101"),
                new Schedule("2025-1", "ENG101", "大学英语", "周四 14:00-16:00", "C101")
        ));
        STUDENT_SCHEDULES.put("2022002", Arrays.asList(
                new Schedule("2025-1", "CS201", "数据结构", "周三 14:00-16:00", "A201"),
                new Schedule("2025-1", "PHY101", "大学物理", "周五 8:00-10:00", "D101")
        ));
    }

    @Override
    public String apply(CourseQueryRequest request, ToolContext toolContext) {
        if (request.operation == null || request.operation.trim().isEmpty()) {
            return "错误：必须指定operation参数";
        }

        try {
            return switch (request.operation.toLowerCase()) {
                case "queryschedule" -> querySchedule(request.studentId, request.semester);
                case "querycoursedetail" -> queryCourseDetail(request.courseId);
                case "queryenrollment" -> queryEnrollment(request.semester);
                default ->
                        "错误：未知操作。支持的操作：querySchedule（查询课程表）、queryCourseDetail（查询课程详情）、queryEnrollment（查询选课信息）";
            };
        } catch (Exception e) {
            return "错误：" + e.getMessage();
        }
    }

    /**
     * 查询学生课程表
     *
     * @param studentId 学生ID
     * @param semester  学期
     * @return 格式化的课程表信息
     */
    private String querySchedule(String studentId, String semester) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return "错误：查询课程表操作必须指定studentId参数";
        }

        List<Schedule> schedules = STUDENT_SCHEDULES.getOrDefault(studentId, Collections.emptyList());
        if (semester != null && !semester.trim().isEmpty()) {
            schedules = schedules.stream()
                    .filter(s -> s.semester.equals(semester))
                    .toList();
        }

        if (schedules.isEmpty()) {
            return String.format("未找到学生%s%s的课程表", studentId,
                    semester != null ? " 在" + semester + "学期" : "");
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("学生%s%s的课程表：\n\n", studentId,
                semester != null ? " （学期：" + semester + "）" : ""));
        for (Schedule schedule : schedules) {
            result.append(String.format("- %s（%s）：%s，地点：%s\n",
                    schedule.courseName, schedule.courseId, schedule.time, schedule.location));
        }
        return result.toString();
    }

    /**
     * 查询课程详细信息
     *
     * @param courseId 课程ID
     * @return 格式化的课程详情
     */
    private String queryCourseDetail(String courseId) {
        if (courseId == null || courseId.trim().isEmpty()) {
            return "错误：查询课程详情操作必须指定courseId参数";
        }

        Course course = COURSES.get(courseId.toUpperCase());
        if (course == null) {
            return "错误：未找到课程：" + courseId;
        }

        return String.format("""
                        课程详情：
                        课程编号：%s
                        课程名称：%s
                        开课学院：%s
                        学分：%d
                        容量：%d
                        上课时间：%s
                        上课地点：%s""",
                course.id, course.name, course.department, course.credits,
                course.capacity, course.time, course.location);
    }

    /**
     * 查询选课信息（可用课程）
     *
     * @param semester 学期
     * @return 格式化的选课信息
     */
    private String queryEnrollment(String semester) {
        StringBuilder result = new StringBuilder();
        result.append("可选课程");
        if (semester != null && !semester.trim().isEmpty()) {
            result.append("（").append(semester).append("学期）");
        }
        result.append("：\n\n");

        for (Course course : COURSES.values()) {
            result.append(String.format("- %s（%s）：%d学分，%s，上课地点：%s\n",
                    course.name, course.id, course.credits, course.time, course.location));
        }
        return result.toString();
    }

    /**
     * 课程查询请求参数类
     */
    public static class CourseQueryRequest {
        @JsonProperty(required = true)
        @JsonPropertyDescription("要执行的操作：querySchedule（查询课程表）、queryCourseDetail（查询课程详情）、queryEnrollment（查询选课信息）")
        public String operation;

        @JsonPropertyDescription("学生ID（查询课程表时必填）")
        public String studentId;

        @JsonPropertyDescription("学期（例如：2024-1）")
        public String semester;

        @JsonPropertyDescription("课程ID（查询课程详情时必填）")
        public String courseId;
    }

    /**
     * 课程实体类
     */
    private static class Course {
        String id;         // 课程编号
        String name;       // 课程名称
        String department; // 开课学院
        int credits;       // 学分
        int capacity;      // 课程容量
        String time;       // 上课时间
        String location;   // 上课地点

        Course(String id, String name, String department, int credits, int capacity, String time, String location) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.credits = credits;
            this.capacity = capacity;
            this.time = time;
            this.location = location;
        }
    }

    /**
     * 课程表实体类
     */
    private static class Schedule {
        String semester;    // 学期
        String courseId;    // 课程编号
        String courseName;  // 课程名称
        String time;        // 上课时间
        String location;    // 上课地点

        Schedule(String semester, String courseId, String courseName, String time, String location) {
            this.semester = semester;
            this.courseId = courseId;
            this.courseName = courseName;
            this.time = time;
            this.location = location;
        }
    }
}