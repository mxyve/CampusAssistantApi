package top.xym.campusassistantapi.module.agent.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.chat.model.ToolContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 用于校园导航、位置查询和路线规划的工具类。
 *
 * @author moqi
 */
public class CampusNavigationTool implements BiFunction<CampusNavigationTool.NavigationRequest, ToolContext, String> {

    public static final String DESCRIPTION = """
            查询校园位置、楼宇信息和导航路线。
            
            支持的操作：
            - queryLocation: 查询指定地点或楼宇的位置信息
            - queryRoute: 查询从一个位置到另一个位置的导航路线
            - queryBuildingInfo: 查询指定楼宇的详细信息
            
            使用示例：
            - 查询位置：operation="queryLocation", placeName="图书馆"
            - 查询路线：operation="queryRoute", from="教学楼A", to="图书馆"
            - 查询楼宇信息：operation="queryBuildingInfo", buildingName="教学楼A"
            """;

    // 模拟数据：校园位置信息
    private static final Map<String, Location> LOCATIONS = new HashMap<>();
    // 模拟数据：楼宇信息
    private static final Map<String, Building> BUILDINGS = new HashMap<>();

    static {
        // 初始化位置数据
        LOCATIONS.put("图书馆", new Location("图书馆", "B栋", "北校区", "正门朝向湖泊"));
        LOCATIONS.put("教学楼A", new Location("教学楼A", "A栋", "中校区", "主广场附近"));
        LOCATIONS.put("教学楼B", new Location("教学楼B", "B栋", "中校区", "A栋旁边"));
        LOCATIONS.put("食堂", new Location("食堂", "C栋", "南校区", "宿舍区附近"));
        LOCATIONS.put("体育馆", new Location("体育馆", "D栋", "东校区", "运动场附近"));
        LOCATIONS.put("学生活动中心", new Location("学生活动中心", "E栋", "中校区", "图书馆和食堂之间"));
        LOCATIONS.put("实验楼", new Location("实验楼", "F栋", "北校区", "图书馆后方"));

        // 初始化楼宇数据
        BUILDINGS.put("教学楼A", new Building("教学楼A", "Building A", "中校区", 5,
                "主教学楼，包含普通教室、阶梯教室和教师办公室"));
        BUILDINGS.put("教学楼B", new Building("教学楼B", "Building B", "中校区", 6,
                "副教学楼，包含专业实验室和计算机机房"));
        BUILDINGS.put("图书馆", new Building("图书馆", "Library", "北校区", 8,
                "主图书馆，包含阅览室、自习区和藏书区"));
        BUILDINGS.put("食堂", new Building("食堂", "Cafeteria", "南校区", 3,
                "学生食堂，提供多种餐饮选择和就餐区"));
        BUILDINGS.put("体育馆", new Building("体育馆", "Gymnasium", "东校区", 2,
                "体育综合体，包含篮球场、游泳池和健身中心"));
    }

    @Override
    public String apply(NavigationRequest request, ToolContext toolContext) {
        if (request.operation == null || request.operation.trim().isEmpty()) {
            return "错误：必须指定operation参数";
        }

        try {
            return switch (request.operation.toLowerCase()) {
                case "querylocation" -> queryLocation(request.placeName);
                case "queryroute" -> queryRoute(request.from, request.to);
                case "querybuildinginfo" -> queryBuildingInfo(request.buildingName);
                default ->
                        "错误：未知操作。支持的操作：queryLocation（查询位置）、queryRoute（查询路线）、queryBuildingInfo（查询楼宇信息）";
            };
        } catch (Exception e) {
            return "错误：" + e.getMessage();
        }
    }

    /**
     * 查询指定地点的位置信息
     *
     * @param placeName 地点名称
     * @return 格式化的位置信息
     */
    private String queryLocation(String placeName) {
        if (placeName == null || placeName.trim().isEmpty()) {
            return "错误：查询位置操作必须指定placeName参数";
        }

        Location location = LOCATIONS.get(placeName);
        if (location == null) {
            // 尝试查找相似位置
            for (String key : LOCATIONS.keySet()) {
                if (key.contains(placeName) || placeName.contains(key)) {
                    location = LOCATIONS.get(key);
                    break;
                }
            }
        }

        if (location == null) {
            return "错误：未找到位置：" + placeName + "。可用位置：" + String.join("、", LOCATIONS.keySet());
        }

        return String.format("""
                        位置信息：
                        名称：%s
                        楼宇编号：%s
                        校区：%s
                        描述：%s""",
                location.name, location.building, location.area, location.description);
    }

    /**
     * 查询从起点到终点的导航路线
     *
     * @param from 起点位置
     * @param to   终点位置
     * @return 格式化的导航路线信息
     */
    private String queryRoute(String from, String to) {
        if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
            return "错误：查询路线操作必须同时指定from和to参数";
        }

        Location fromLocation = LOCATIONS.get(from);
        Location toLocation = LOCATIONS.get(to);

        if (fromLocation == null) {
            return "错误：未找到起点位置：" + from;
        }
        if (toLocation == null) {
            return "错误：未找到终点位置：" + to;
        }

        if (from.equals(to)) {
            return "您已经在" + from + "位置";
        }

        // 基于校区计算简单路线
        String route = calculateRoute(fromLocation, toLocation);
        int estimatedTime = calculateEstimatedTime(fromLocation, toLocation);

        return String.format("""
                        导航路线：
                        起点：%s（%s）
                        终点：%s（%s）
                        路线：%s
                        预计耗时：%d分钟""",
                from, fromLocation.area, to, toLocation.area, route, estimatedTime);
    }

    /**
     * 查询指定楼宇的详细信息
     *
     * @param buildingName 楼宇名称
     * @return 格式化的楼宇信息
     */
    private String queryBuildingInfo(String buildingName) {
        if (buildingName == null || buildingName.trim().isEmpty()) {
            return "错误：查询楼宇信息操作必须指定buildingName参数";
        }

        Building building = BUILDINGS.get(buildingName);
        if (building == null) {
            return "错误：未找到楼宇：" + buildingName + "。可用楼宇：" + String.join("、", BUILDINGS.keySet());
        }

        return String.format("""
                        楼宇信息：
                        名称：%s
                        英文名称：%s
                        校区：%s
                        楼层数：%d
                        描述：%s""",
                building.name, building.englishName, building.area, building.floors, building.description);
    }

    /**
     * 计算导航路线描述
     *
     * @param from 起点位置
     * @param to   终点位置
     * @return 路线描述
     */
    private String calculateRoute(Location from, Location to) {
        if (from.area.equals(to.area)) {
            return String.format("在%s区域内步行即可到达", from.area);
        } else {
            return String.format("从%s出发，沿校园主干道前往%s", from.area, to.area);
        }
    }

    /**
     * 计算预计耗时（分钟）
     *
     * @param from 起点位置
     * @param to   终点位置
     * @return 预计耗时
     */
    private int calculateEstimatedTime(Location from, Location to) {
        if (from.area.equals(to.area)) {
            return 5; // 同校区内5分钟
        } else {
            return 10; // 跨校区10分钟
        }
    }

    /**
     * 导航查询请求参数类
     */
    public static class NavigationRequest {
        @JsonProperty(required = true)
        @JsonPropertyDescription("要执行的操作：queryLocation（查询位置）、queryRoute（查询路线）、queryBuildingInfo（查询楼宇信息）")
        public String operation;

        @JsonPropertyDescription("地点名称（查询位置时使用）")
        public String placeName;

        @JsonPropertyDescription("起点位置（查询路线时使用）")
        public String from;

        @JsonPropertyDescription("终点位置（查询路线时使用）")
        public String to;

        @JsonPropertyDescription("楼宇名称（查询楼宇信息时使用）")
        public String buildingName;
    }

    /**
     * 位置实体类
     */
    private static class Location {
        String name;        // 地点名称
        String building;    // 楼宇编号
        String area;        // 校区
        String description; // 描述信息

        Location(String name, String building, String area, String description) {
            this.name = name;
            this.building = building;
            this.area = area;
            this.description = description;
        }
    }

    /**
     * 楼宇实体类
     */
    private static class Building {
        String name;         // 楼宇名称
        String englishName;  // 英文名称
        String area;         // 校区
        int floors;          // 楼层数
        String description;  // 描述信息

        Building(String name, String englishName, String area, int floors, String description) {
            this.name = name;
            this.englishName = englishName;
            this.area = area;
            this.floors = floors;
            this.description = description;
        }
    }
}