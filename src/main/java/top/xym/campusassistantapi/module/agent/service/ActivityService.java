package top.xym.campusassistantapi.module.agent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.campusassistantapi.module.agent.model.entity.Activity;

import java.util.List;

public interface ActivityService extends IService<Activity> {
    /**
     * 查询活动列表
     */
    List<Activity> getActivityList(String category, String date);

    /**
     * 查询活动详情
     */
    Activity getActivityById(String activityId);

    /**
     * 查询报名状态
     */
    boolean isRegistered(String activityId, String studentId);

    /**
     * 获取已报名人数
     */
    long getRegisteredCount(String activityId);
}