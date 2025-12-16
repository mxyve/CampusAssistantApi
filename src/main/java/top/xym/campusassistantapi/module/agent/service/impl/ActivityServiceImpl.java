package top.xym.campusassistantapi.module.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.xym.campusassistantapi.module.agent.model.entity.Activity;
import top.xym.campusassistantapi.module.agent.model.entity.ActivityRegistration;
import top.xym.campusassistantapi.module.agent.mapper.ActivityMapper;
import top.xym.campusassistantapi.module.agent.mapper.ActivityRegistrationMapper;
import top.xym.campusassistantapi.module.agent.service.ActivityService;

import java.util.List;

/**
 * 活动服务实现类
 *
 * @author moqi
 */
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    private final ActivityRegistrationMapper activityRegistrationMapper;

    @Override
    public List<Activity> getActivityList(String category, String date) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.trim().isEmpty()) {
            wrapper.eq(Activity::getCategory, category);
        }
        if (date != null && !date.trim().isEmpty()) {
            wrapper.like(Activity::getDateTime, date);
        }
        return list(wrapper);
    }

    @Override
    public Activity getActivityById(String activityId) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getActivityId, activityId);
        return getOne(wrapper);
    }

    @Override
    public boolean isRegistered(String activityId, String studentId) {
        LambdaQueryWrapper<ActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegistration::getActivityId, activityId).eq(ActivityRegistration::getStudentId, studentId);
        return activityRegistrationMapper.selectCount(wrapper) > 0;
    }

    @Override
    public long getRegisteredCount(String activityId) {
        LambdaQueryWrapper<ActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegistration::getActivityId, activityId);
        return activityRegistrationMapper.selectCount(wrapper);
    }
}

