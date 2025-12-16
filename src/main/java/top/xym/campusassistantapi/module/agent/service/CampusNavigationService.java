package top.xym.campusassistantapi.module.agent.service;

import top.xym.campusassistantapi.module.agent.model.entity.Building;
import top.xym.campusassistantapi.module.agent.model.entity.Location;

public interface CampusNavigationService {
    /**
     * 查询位置信息
     */
    Location getLocationByName(String placeName);

    /**
     * 查询建筑信息
     */
    Building getBuildingByName(String buildingName);

    /**
     * 计算路线
     */
    String calculateRoute(String from, String to);

    /**
     * 计算预计时间
     */
    int calculateEstimatedTime(String from, String to);
}
