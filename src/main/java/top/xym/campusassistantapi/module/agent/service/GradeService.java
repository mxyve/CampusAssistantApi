package top.xym.campusassistantapi.module.agent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.campusassistantapi.module.agent.model.entity.Grade;

import java.util.List;

public interface GradeService extends IService<Grade> {
    /**
     * 查询学期成绩
     */
    List<Grade> getGradesByStudentId(String studentId, String semester);

    /**
     * 查询课程成绩
     */
    Grade getGradeByStudentIdAndCourseId(String studentId, String courseId);

    /**
     * 查询成绩统计
     */
    GradeStatistics getGradeStatistics(String studentId);

    /**
     * 成绩统计结果
     */
    class GradeStatistics {
        public int totalCourses;
        public int totalCredits;
        public double averageScore;
        public double gpa;
    }
}
