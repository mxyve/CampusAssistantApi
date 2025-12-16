package top.xym.campusassistantapi.module.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.xym.campusassistantapi.module.agent.model.entity.Building;
import top.xym.campusassistantapi.module.agent.model.entity.Location;
import top.xym.campusassistantapi.module.agent.mapper.BuildingMapper;
import top.xym.campusassistantapi.module.agent.mapper.LocationMapper;
import top.xym.campusassistantapi.module.agent.service.CampusNavigationService;

import java.util.List;

/**
 * 校园导航服务实现类
 *
 * @author moqi
 */
@Service
@RequiredArgsConstructor
public class CampusNavigationServiceImpl implements CampusNavigationService {

    private final LocationMapper locationMapper;
    private final BuildingMapper buildingMapper;


    @Override
    public Location getLocationByName(String placeName) {
        LambdaQueryWrapper<Location> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Location::getName, placeName);
        Location location = locationMapper.selectOne(wrapper);

        // 如果精确匹配失败，尝试模糊匹配
        if (location == null) {
            List<Location> locations = locationMapper.selectList(null);
            for (Location loc : locations) {
                if (loc.getName().contains(placeName) || placeName.contains(loc.getName())) {
                    return loc;
                }
            }
        }

        return location;
    }

    @Override
    public Building getBuildingByName(String buildingName) {
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Building::getName, buildingName);
        return buildingMapper.selectOne(wrapper);
    }

    @Override
    public String calculateRoute(String from, String to) {
        Location fromLocation = getLocationByName(from);
        Location toLocation = getLocationByName(to);

        if (fromLocation == null || toLocation == null) {
            return "无法计算路线";
        }

        if (fromLocation.getArea().equals(toLocation.getArea())) {
            return String.format("在 %s 区域内步行", fromLocation.getArea());
        } else {
            return String.format("从 %s 步行到 %s，经过校园主干道", fromLocation.getArea(), toLocation.getArea());
        }
    }

    @Override
    public int calculateEstimatedTime(String from, String to) {
        Location fromLocation = getLocationByName(from);
        Location toLocation = getLocationByName(to);

        if (fromLocation == null || toLocation == null) {
            return 0;
        }

        if (fromLocation.getArea().equals(toLocation.getArea())) {
            return 5; // 同一区域内5分钟
        } else {
            return 10; // 不同区域间10分钟
        }
    }
}

