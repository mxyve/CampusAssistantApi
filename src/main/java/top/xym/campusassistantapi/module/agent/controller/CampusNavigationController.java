package top.xym.campusassistantapi.module.agent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.xym.campusassistantapi.module.agent.model.entity.Building;
import top.xym.campusassistantapi.module.agent.model.entity.Location;
import top.xym.campusassistantapi.module.agent.service.CampusNavigationService;

/**
 * 校园导航控制器
 *
 * @author moqi
 */
@RestController
@RequestMapping("/api/navigation")
@RequiredArgsConstructor
@Tag(name = "校园导航相关", description = "校园导航相关接口")
public class CampusNavigationController {

    private final CampusNavigationService campusNavigationService;

    /**
     * 查询位置信息
     */
    @GetMapping("/location")
    @Operation(summary = "查询位置信息")
    public Location getLocation(@RequestParam String placeName) {
        return campusNavigationService.getLocationByName(placeName);
    }

    /**
     * 查询建筑信息
     */
    @GetMapping("/building")
    @Operation(summary = "查询建筑信息")
    public Building getBuilding(@RequestParam String buildingName) {
        return campusNavigationService.getBuildingByName(buildingName);
    }

    /**
     * 查询路线
     */
    @GetMapping("/route")
    @Operation(summary = "查询路线")
    public RouteResponse getRoute(@RequestParam String from,
                                  @RequestParam String to) {
        String route = campusNavigationService.calculateRoute(from, to);
        int estimatedTime = campusNavigationService.calculateEstimatedTime(from, to);
        return new RouteResponse(route, estimatedTime);
    }

    /**
     * 路线响应
     */
    public static class RouteResponse {
        public String route;
        public int estimatedTime;

        public RouteResponse(String route, int estimatedTime) {
            this.route = route;
            this.estimatedTime = estimatedTime;
        }
    }
}