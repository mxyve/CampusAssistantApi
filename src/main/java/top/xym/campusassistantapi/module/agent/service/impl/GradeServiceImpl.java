package top.xym.campusassistantapi.module.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xym.campusassistantapi.module.agent.model.entity.Grade;
import top.xym.campusassistantapi.module.agent.mapper.GradeMapper;
import top.xym.campusassistantapi.module.agent.service.GradeService;

import java.util.List;
import java.util.Map;

/**
 * 成绩服务实现类
 *
 * @author moqi
 */
@Service
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    private static final Map<String, Double> GRADE_POINTS = Map.ofEntries(
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

    @Override
    public List<Grade> getGradesByStudentId(String studentId, String semester) {
        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Grade::getStudentId, studentId);
        if (semester != null && !semester.trim().isEmpty()) {
            wrapper.eq(Grade::getSemester, semester);
        }
        return list(wrapper);
    }

    @Override
    public Grade getGradeByStudentIdAndCourseId(String studentId, String courseId) {
        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Grade::getStudentId, studentId)
                .eq(Grade::getCourseId, courseId);
        return getOne(wrapper);
    }

    @Override
    public GradeStatistics getGradeStatistics(String studentId) {
        List<Grade> grades = getGradesByStudentId(studentId, null);

        GradeStatistics stats = new GradeStatistics();
        stats.totalCourses = grades.size();

        if (grades.isEmpty()) {
            return stats;
        }

        double totalPoints = 0;
        int totalCredits = 0;
        double weightedSum = 0;

        for (Grade grade : grades) {
            double points = GRADE_POINTS.getOrDefault(grade.getGrade(), 0.0);
            totalPoints += grade.getScore();
            totalCredits += grade.getCredits();
            weightedSum += points * grade.getCredits();
        }

        stats.totalCredits = totalCredits;
        stats.averageScore = totalPoints / grades.size();
        stats.gpa = totalCredits > 0 ? weightedSum / totalCredits : 0.0;

        return stats;
    }
}
