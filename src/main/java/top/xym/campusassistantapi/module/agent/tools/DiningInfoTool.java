package top.xym.campusassistantapi.module.agent.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.chat.model.ToolContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 用于查询食堂信息的工具类，包括菜单、营业时间和位置。
 *
 * @author moqi
 */
public class DiningInfoTool implements BiFunction<DiningInfoTool.DiningRequest, ToolContext, String> {

    public static final String DESCRIPTION = """
            查询食堂相关信息，包括菜单、营业时间和位置。
            
            支持的操作：
            - queryMenu: 查询指定食堂和日期的菜单
            - queryOperatingHours: 查询食堂的营业时间
            - queryDiningHallLocation: 查询食堂的位置信息
            
            使用示例：
            - 查询菜单：operation="queryMenu", diningHall="第一食堂", date="2025-09-15"
            - 查询营业时间：operation="queryOperatingHours", diningHall="第一食堂"
            - 查询位置：operation="queryDiningHallLocation", diningHall="第一食堂"
            """;

    // 模拟数据：食堂信息
    private static final Map<String, DiningHall> DINING_HALLS = new HashMap<>();
    // 模拟数据：菜单信息
    private static final Map<String, Map<String, Menu>> MENUS = new HashMap<>();

    static {
        // 初始化模拟食堂数据
        DINING_HALLS.put("第一食堂", new DiningHall("第一食堂", "南校区", "宿舍区附近",
                "07:00-09:00, 11:00-13:00, 17:00-19:00"));
        DINING_HALLS.put("第二食堂", new DiningHall("第二食堂", "中校区", "教学楼附近",
                "07:00-09:00, 11:00-13:30, 17:00-19:30"));
        DINING_HALLS.put("清真食堂", new DiningHall("清真食堂", "南校区", "第一食堂旁",
                "07:00-09:00, 11:00-13:00, 17:00-19:00"));

        // 初始化模拟菜单数据
        Map<String, Menu> menu20250915 = new HashMap<>();
        menu20250915.put("第一食堂", new Menu("第一食堂", "2025-09-15", Arrays.asList(
                new MenuItem("红烧肉", 12.0, "荤菜"),
                new MenuItem("麻婆豆腐", 8.0, "素菜"),
                new MenuItem("西红柿鸡蛋", 10.0, "素菜"),
                new MenuItem("宫保鸡丁", 15.0, "荤菜"),
                new MenuItem("米饭", 1.0, "主食")
        )));
        menu20250915.put("第二食堂", new Menu("第二食堂", "2025-09-15", Arrays.asList(
                new MenuItem("糖醋里脊", 14.0, "荤菜"),
                new MenuItem("地三鲜", 9.0, "素菜"),
                new MenuItem("鱼香肉丝", 13.0, "荤菜"),
                new MenuItem("紫菜蛋花汤", 3.0, "汤类"),
                new MenuItem("面条", 8.0, "主食")
        )));
        MENUS.put("2025-09-15", menu20250915);
    }

    @Override
    public String apply(DiningRequest request, ToolContext toolContext) {
        if (request.operation == null || request.operation.trim().isEmpty()) {
            return "错误：必须指定operation参数";
        }

        try {
            return switch (request.operation.toLowerCase()) {
                case "querymenu" -> queryMenu(request.diningHall, request.date);
                case "queryoperatinghours" -> queryOperatingHours(request.diningHall);
                case "querydininghalllocation" -> queryDiningHallLocation(request.diningHall);
                default ->
                        "错误：未知操作。支持的操作：queryMenu（查询菜单）、queryOperatingHours（查询营业时间）、queryDiningHallLocation（查询食堂位置）";
            };
        } catch (Exception e) {
            return "错误：" + e.getMessage();
        }
    }

    /**
     * 查询指定食堂和日期的菜单
     *
     * @param diningHall 食堂名称
     * @param date       日期（格式：YYYY-MM-DD）
     * @return 格式化的菜单信息
     */
    private String queryMenu(String diningHall, String date) {
        if (diningHall == null || diningHall.trim().isEmpty()) {
            return "错误：查询菜单操作必须指定diningHall参数";
        }

        String queryDate = date != null && !date.trim().isEmpty() ? date : "2025-09-15";
        Map<String, Menu> menus = MENUS.getOrDefault(queryDate, new HashMap<>());
        Menu menu = menus.get(diningHall);

        if (menu == null) {
            return String.format("未找到%s %s的菜单。可用食堂：%s",
                    diningHall, queryDate, String.join("、", DINING_HALLS.keySet()));
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("%s %s菜单：\n\n", diningHall, queryDate));
        for (MenuItem item : menu.items) {
            result.append(String.format("- %s (¥%.2f) [%s]\n", item.name, item.price, item.category));
        }
        return result.toString();
    }

    /**
     * 查询食堂营业时间
     *
     * @param diningHall 食堂名称（为空时返回所有食堂营业时间）
     * @return 格式化的营业时间信息
     */
    private String queryOperatingHours(String diningHall) {
        if (diningHall == null || diningHall.trim().isEmpty()) {
            // 返回所有食堂的营业时间
            StringBuilder result = new StringBuilder();
            result.append("所有食堂营业时间：\n\n");
            for (DiningHall hall : DINING_HALLS.values()) {
                result.append(String.format("%s：\n", hall.name));
                result.append(String.format("  位置：%s（%s）\n", hall.location, hall.area));
                result.append(String.format("  营业时间：%s\n\n", hall.operatingHours));
            }
            return result.toString();
        }

        DiningHall hall = DINING_HALLS.get(diningHall);
        if (hall == null) {
            return "错误：未找到食堂：" + diningHall + "。可用食堂：" + String.join("、", DINING_HALLS.keySet());
        }

        return String.format("""
                        %s营业时间：
                        位置：%s（%s）
                        营业时间：%s""",
                hall.name, hall.location, hall.area, hall.operatingHours);
    }

    /**
     * 查询食堂位置信息
     *
     * @param diningHall 食堂名称（为空时返回所有食堂位置）
     * @return 格式化的位置信息
     */
    private String queryDiningHallLocation(String diningHall) {
        if (diningHall == null || diningHall.trim().isEmpty()) {
            StringBuilder result = new StringBuilder();
            result.append("食堂位置信息：\n\n");
            for (DiningHall hall : DINING_HALLS.values()) {
                result.append(String.format("- %s：%s（%s）\n", hall.name, hall.location, hall.area));
            }
            return result.toString();
        }

        DiningHall hall = DINING_HALLS.get(diningHall);
        if (hall == null) {
            return "错误：未找到食堂：" + diningHall + "。可用食堂：" + String.join("、", DINING_HALLS.keySet());
        }

        return String.format("""
                        %s位置信息：
                        校区：%s
                        具体位置：%s
                        营业时间：%s""",
                hall.name, hall.area, hall.location, hall.operatingHours);
    }

    /**
     * 食堂信息查询请求参数类
     */
    public static class DiningRequest {
        @JsonProperty(required = true)
        @JsonPropertyDescription("要执行的操作：queryMenu（查询菜单）、queryOperatingHours（查询营业时间）、queryDiningHallLocation（查询食堂位置）")
        public String operation;

        @JsonPropertyDescription("食堂名称（例如：第一食堂、第二食堂）")
        public String diningHall;

        @JsonPropertyDescription("日期（格式：YYYY-MM-DD，查询菜单时可选，默认当天）")
        public String date;
    }

    /**
     * 食堂实体类
     */
    private static class DiningHall {
        String name;            // 食堂名称
        String area;            // 校区
        String location;        // 具体位置
        String operatingHours;  // 营业时间

        DiningHall(String name, String area, String location, String operatingHours) {
            this.name = name;
            this.area = area;
            this.location = location;
            this.operatingHours = operatingHours;
        }
    }

    /**
     * 菜单实体类
     */
    private static class Menu {
        String diningHall;  // 食堂名称
        String date;        // 日期
        List<MenuItem> items; // 菜品列表

        Menu(String diningHall, String date, List<MenuItem> items) {
            this.diningHall = diningHall;
            this.date = date;
            this.items = items;
        }
    }

    /**
     * 菜品实体类
     */
    private static class MenuItem {
        String name;        // 菜品名称
        double price;       // 价格
        String category;    // 分类（荤菜/素菜/主食/汤类）

        MenuItem(String name, double price, String category) {
            this.name = name;
            this.price = price;
            this.category = category;
        }
    }
}