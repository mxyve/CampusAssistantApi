package top.xym.campusassistantapi.module.agent.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.chat.model.ToolContext;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * 用于查询学生成绩和成绩统计的工具类。
 *
 * @author moqi
 */
public class GradeQueryTool implements BiFunction<GradeQueryTool.GradeQueryRequest, ToolContext, String> {

    public static final String DESCRIPTION = """
            查询学生成绩信息，包括学期成绩、课程成绩和成绩统计。
            
            支持的操作：
            - querySemesterGrades: 查询指定学期的所有成绩
            - queryCourseGrade: 查询指定课程的成绩
            - queryGradeStatistics: 查询成绩统计信息（平均绩点、平均分等）
            
            使用示例：
            - 查询学期成绩：operation="querySemesterGrades", studentId="2022001", semester="2025-1"
            - 查询课程成绩：operation="queryCourseGrade", studentId="2022001", courseId="CS101"
            - 查询成绩统计：operation="queryGradeStatistics", studentId="2022001"
            """;

    // 模拟数据：成绩数据库
    private static final Map<String, List<Grade>> STUDENT_GRADES = new HashMap<>();

    static {
        // 初始化学生2022001的模拟成绩数据
        STUDENT_GRADES.put("2022001", Arrays.asList(
                new Grade("2025-1", "CS101", "计算机科学导论", 85, 3, "B+"),
                new Grade("2025-1", "MATH101", "高等数学", 92, 4, "A"),
                new Grade("2025-1", "ENG101", "大学英语", 78, 2, "B"),
                new Grade("2025-1", "CS201", "数据结构", 88, 4, "A-"),
                new Grade("2025-1", "PHY101", "大学物理", 82, 3, "B+")
        ));

        // 初始化学生2022002的模拟成绩数据
        STUDENT_GRADES.put("2022002", Arrays.asList(
                new Grade("2025-1", "CS201", "数据结构", 95, 4, "A"),
                new Grade("2025-1", "PHY101", "大学物理", 90, 3, "A-"),
                new Grade("2025-1", "MATH101", "高等数学", 87, 4, "B+")
        ));
    }

    @Override
    public String apply(GradeQueryRequest request, ToolContext toolContext) {
        if (request.operation == null || request.operation.trim().isEmpty()) {
            return "错误：必须指定operation参数";
        }

        if (request.studentId == null || request.studentId.trim().isEmpty()) {
            return "错误：必须指定studentId参数";
        }

        try {
            return switch (request.operation.toLowerCase()) {
                case "querysemestergrades" -> querySemesterGrades(request.studentId, request.semester);
                case "querycoursegrade" -> queryCourseGrade(request.studentId, request.courseId);
                case "querygradestatistics" -> queryGradeStatistics(request.studentId);
                default ->
                        "错误：未知操作。支持的操作：querySemesterGrades（查询学期成绩）、queryCourseGrade（查询课程成绩）、queryGradeStatistics（查询成绩统计）";
            };
        } catch (Exception e) {
            return "错误：" + e.getMessage();
        }
    }

    /**
     * 查询学生指定学期的成绩
     *
     * @param studentId 学生ID
     * @param semester  学期
     * @return 格式化的学期成绩信息
     */
    private String querySemesterGrades(String studentId, String semester) {
        List<Grade> grades = STUDENT_GRADES.getOrDefault(studentId, Collections.emptyList());
        if (semester != null && !semester.trim().isEmpty()) {
            grades = grades.stream().filter(g -> g.semester.equals(semester)).collect(Collectors.toList());
        }

        if (grades.isEmpty()) {
            return String.format("未找到学生%s%s的成绩信息", studentId, semester != null ? " 在" + semester + "学期" : "");
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("学生%s%s的成绩信息：\n\n", studentId, semester != null ? " （学期：" + semester + "）" : ""));
        for (Grade grade : grades) {
            result.append(String.format("- %s（%s）：分数：%d，等级：%s，学分：%d\n",
                    grade.courseName, grade.courseId, grade.score, grade.grade, grade.credits));
        }
        return result.toString();
    }

    /**
     * 查询学生指定课程的成绩
     *
     * @param studentId 学生ID
     * @param courseId  课程ID
     * @return 格式化的课程成绩详情
     */
    private String queryCourseGrade(String studentId, String courseId) {
        if (courseId == null || courseId.trim().isEmpty()) {
            return "错误：查询课程成绩操作必须指定courseId参数";
        }

        List<Grade> grades = STUDENT_GRADES.getOrDefault(studentId, Collections.emptyList());
        Grade grade = grades.stream().filter(g -> g.courseId.equalsIgnoreCase(courseId)).findFirst().orElse(null);

        if (grade == null) {
            return String.format("未找到学生%s的%s课程成绩", studentId, courseId);
        }

        return String.format("""
                        课程成绩详情：
                        学生ID：%s
                        课程名称：%s（%s）
                        学期：%s
                        分数：%d
                        等级：%s
                        学分：%d""",
                studentId, grade.courseName, grade.courseId, grade.semester,
                grade.score, grade.grade, grade.credits);
    }

    /**
     * 查询学生成绩统计信息
     *
     * @param studentId 学生ID
     * @return 格式化的成绩统计信息
     */
    private String queryGradeStatistics(String studentId) {
        List<Grade> grades = STUDENT_GRADES.getOrDefault(studentId, Collections.emptyList());
        if (grades.isEmpty()) {
            return "未找到学生" + studentId + "的成绩信息";
        }

        double totalPoints = 0;
        int totalCredits = 0;
        double weightedSum = 0;

        // 成绩等级对应的绩点映射
        Map<String, Double> gradePoints = Map.ofEntries(
                Map.entry("A", 4.0),
                Map.entry("A-", 3.7),
                Map.entry("B+", 3.3),
                Map.entry("B", 3.0),
                Map.entry("B-", 2.7),
                Map.entry("C+", 2.3),
                Map.entry("C", 2.0),
                Map.entry("C-", 1.7),
                Map.entry("D+", 1.3),
                Map.entry("D", 1.0),
                Map.entry("F", 0.0)
        );

        for (Grade grade : grades) {
            double points = gradePoints.getOrDefault(grade.grade, 0.0);
            totalPoints += grade.score;
            totalCredits += grade.credits;
            weightedSum += points * grade.credits;
        }

        double averageScore = totalPoints / grades.size();
        double gpa = totalCredits > 0 ? weightedSum / totalCredits : 0.0;

        return String.format("""
                        学生%s的成绩统计信息：
                        
                        总课程数：%d
                        总学分：%d
                        平均分：%.2f
                        平均绩点（GPA）：%.2f""",
                studentId, grades.size(), totalCredits, averageScore, gpa);
    }

    /**
     * 成绩查询请求参数类
     */
    public static class GradeQueryRequest {
        @JsonProperty(required = true)
        @JsonPropertyDescription("要执行的操作：querySemesterGrades（查询学期成绩）、queryCourseGrade（查询课程成绩）、queryGradeStatistics（查询成绩统计）")
        public String operation;

        @JsonProperty(required = true)
        @JsonPropertyDescription("学生ID（所有操作都必填）")
        public String studentId;

        @JsonPropertyDescription("学期（例如：2024-1，仅查询学期成绩时使用）")
        public String semester;

        @JsonPropertyDescription("课程ID（查询课程成绩时必填）")
        public String courseId;
    }

    /**
     * 成绩实体类
     */
    private static class Grade {
        String semester;    // 学期
        String courseId;    // 课程编号
        String courseName;  // 课程名称
        int score;          // 分数
        int credits;        // 学分
        String grade;       // 成绩等级

        Grade(String semester, String courseId, String courseName, int score, int credits, String grade) {
            this.semester = semester;
            this.courseId = courseId;
            this.courseName = courseName;
            this.score = score;
            this.credits = credits;
            this.grade = grade;
        }
    }
}