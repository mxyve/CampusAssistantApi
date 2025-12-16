package top.xym.campusassistantapi.module.agent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xym.campusassistantapi.module.agent.model.entity.Activity;
import top.xym.campusassistantapi.module.agent.service.ActivityService;

import java.util.List;

/**
 * 活动控制器
 *
 * @author moqi
 */
@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
@Tag(name = "活动相关", description = "活动相关接口")
public class ActivityController {

    private final ActivityService activityService;

    /**
     * 查询活动列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询活动列表")
    public List<Activity> getActivityList(@RequestParam(required = false) String category,
                                          @RequestParam(required = false) String date) {
        return activityService.getActivityList(category, date);
    }

    /**
     * 查询活动详情
     */
    @GetMapping("/{activityId}")
    @Operation(summary = "查询活动详情")
    public Activity getActivity(@PathVariable String activityId) {
        return activityService.getActivityById(activityId);
    }

    /**
     * 查询报名状态
     */
    @GetMapping("/registration")
    @Operation(summary = "查询报名状态")
    public RegistrationResponse checkRegistration(@RequestParam String activityId,
                                                  @RequestParam String studentId) {
        boolean isRegistered = activityService.isRegistered(activityId, studentId);
        long registeredCount = activityService.getRegisteredCount(activityId);
        Activity activity = activityService.getActivityById(activityId);
        long availableSpots = activity != null ? activity.getCapacity() - registeredCount : 0;
        return new RegistrationResponse(isRegistered, registeredCount, availableSpots);
    }

    /**
     * 报名响应
     */
    public static class RegistrationResponse {
        public boolean isRegistered;
        public long registeredCount;
        public long availableSpots;

        public RegistrationResponse(boolean isRegistered, long registeredCount, long availableSpots) {
            this.isRegistered = isRegistered;
            this.registeredCount = registeredCount;
            this.availableSpots = availableSpots;
        }
    }
}