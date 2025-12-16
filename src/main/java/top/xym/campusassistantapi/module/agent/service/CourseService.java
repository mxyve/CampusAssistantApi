package top.xym.campusassistantapi.module.agent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.campusassistantapi.module.agent.model.entity.Course;
import top.xym.campusassistantapi.module.agent.model.entity.Schedule;

import java.util.List;

public interface CourseService extends IService<Course> {
    /**
     * 查询学生课表
     */
    List<Schedule> getScheduleByStudentId(String studentId, String semester);

    /**
     * 查询课程详情
     */
    Course getCourseByCourseId(String courseId);

    /**
     * 查询所有可选课程
     */
    List<Course> getAllCourses();
}