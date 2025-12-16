package top.xym.campusassistantapi.module.agent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.xym.campusassistantapi.module.agent.model.entity.Grade;
import top.xym.campusassistantapi.module.agent.service.GradeService;

import java.util.List;

/**
 * 成绩控制器
 *
 * @author moqi
 */
@RestController
@RequestMapping("/api/grade")
@RequiredArgsConstructor
@Tag(name = "成绩相关", description = "成绩相关接口")
public class GradeController {

    private final GradeService gradeService;

    /**
     * 查询学期成绩
     */
    @GetMapping("/semester")
    @Operation(summary = "查询学期成绩")
    public List<Grade> getSemesterGrades(@RequestParam String studentId,
                                         @RequestParam(required = false) String semester) {
        return gradeService.getGradesByStudentId(studentId, semester);
    }

    /**
     * 查询课程成绩
     */
    @GetMapping("/course")
    @Operation(summary = "查询课程成绩")
    public Grade getCourseGrade(@RequestParam String studentId,
                                @RequestParam String courseId) {
        return gradeService.getGradeByStudentIdAndCourseId(studentId, courseId);
    }

    /**
     * 查询成绩统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "查询成绩统计")
    public GradeService.GradeStatistics getGradeStatistics(@RequestParam String studentId) {
        return gradeService.getGradeStatistics(studentId);
    }
}
