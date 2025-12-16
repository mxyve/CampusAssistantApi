package top.xym.campusassistantapi.module.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.xym.campusassistantapi.module.agent.model.entity.Course;
import top.xym.campusassistantapi.module.agent.model.entity.Schedule;
import top.xym.campusassistantapi.module.agent.mapper.CourseMapper;
import top.xym.campusassistantapi.module.agent.mapper.ScheduleMapper;
import top.xym.campusassistantapi.module.agent.service.CourseService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final ScheduleMapper scheduleMapper;

    @Override
    public List<Schedule> getScheduleByStudentId(String studentId, String semester) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getStudentId, studentId);
        if (semester != null && !semester.trim().isEmpty()) {
            wrapper.eq(Schedule::getSemester, semester);
        }
        return scheduleMapper.selectList(wrapper);
    }

    @Override
    public Course getCourseByCourseId(String courseId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseId, courseId);
        return getOne(wrapper);
    }

    @Override
    public List<Course> getAllCourses() {
        return list();
    }
}