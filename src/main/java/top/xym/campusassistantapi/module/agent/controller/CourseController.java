package top.xym.campusassistantapi.module.agent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xym.campusassistantapi.module.agent.model.entity.Course;
import top.xym.campusassistantapi.module.agent.model.entity.Schedule;
import top.xym.campusassistantapi.module.agent.service.CourseService;

import java.util.List;

/**
 * 课程控制器
 *
 * @author moqi
 */
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
@Tag(name = "课程相关", description = "课程相关接口")
public class CourseController {

    private final CourseService courseService;

    /**
     * 查询学生课表
     */
    @GetMapping("/schedule")
    @Operation(summary = "查询学生课表")
    public List<Schedule> getSchedule(@RequestParam String studentId,
                                      @RequestParam(required = false) String semester) {
        return courseService.getScheduleByStudentId(studentId, semester);
    }

    /**
     * 查询课程详情
     */
    @GetMapping("/{courseId}")
    @Operation(summary = "查询课程详情")
    public Course getCourse(@PathVariable String courseId) {
        return courseService.getCourseByCourseId(courseId);
    }

    /**
     * 查询所有可选课程
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有可选课程")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }
}
