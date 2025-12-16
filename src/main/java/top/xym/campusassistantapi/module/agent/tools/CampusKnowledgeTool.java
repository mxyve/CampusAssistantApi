package top.xym.campusassistantapi.module.agent.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.chat.model.ToolContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 用于解答校园知识问题的工具类，包括规章制度、政策和常见问题。
 *
 * @author moqi
 */
public class CampusKnowledgeTool implements BiFunction<CampusKnowledgeTool.KnowledgeRequest, ToolContext, String> {

    public static final String DESCRIPTION = """
            解答校园知识相关问题，包括规章制度、政策和常见问题。
            
            支持的操作：
            - queryRegulation: 查询校园规章制度和政策
            - queryFAQ: 解答常见问题
            - queryGeneralKnowledge: 解答校园通用知识问题
            
            使用示例：
            - 查询规章制度：operation="queryRegulation", topic="选课"
            - 查询常见问题：operation="queryFAQ", question="如何办理学生证"
            - 查询通用知识：operation="queryGeneralKnowledge", question="图书馆开放时间"
            """;

    // 模拟数据：知识库
    private static final Map<String, String> REGULATIONS = new HashMap<>();
    private static final Map<String, String> FAQS = new HashMap<>();
    private static final Map<String, String> GENERAL_KNOWLEDGE = new HashMap<>();

    static {
        // 初始化规章制度数据
        REGULATIONS.put("选课", "选课规定：\n" +
                "1. 每学期选课时间通常在学期开始前2周开始\n" +
                "2. 学生需在规定时间内完成选课，逾期不予补选\n" +
                "3. 选课学分上限为30学分，下限为15学分\n" +
                "4. 必修课必须选择，选修课可根据兴趣选择\n" +
                "5. 选课结果可在选课系统查询");

        REGULATIONS.put("考试", "考试规定：\n" +
                "1. 考试时间由教务处统一安排\n" +
                "2. 学生需携带学生证和身份证参加考试\n" +
                "3. 迟到15分钟以上不得进入考场\n" +
                "4. 考试作弊将受到严肃处理，包括取消成绩和纪律处分\n" +
                "5. 因病或其他原因无法参加考试需提前申请缓考");

        REGULATIONS.put("宿舍", "宿舍管理规定：\n" +
                "1. 宿舍楼晚上23:00关门，早上6:00开门\n" +
                "2. 禁止在宿舍内使用大功率电器\n" +
                "3. 保持宿舍卫生，定期进行卫生检查\n" +
                "4. 禁止在宿舍内饲养宠物\n" +
                "5. 访客需在宿管处登记");

        // 初始化常见问题数据
        FAQS.put("如何办理学生证", "学生证办理流程：\n" +
                "1. 携带身份证和录取通知书到学生处\n" +
                "2. 填写学生证申请表\n" +
                "3. 提交一寸免冠照片\n" +
                "4. 缴纳工本费10元\n" +
                "5. 3-5个工作日后领取学生证");

        FAQS.put("如何申请奖学金", "奖学金申请流程：\n" +
                "1. 关注学校官网发布的奖学金申请通知\n" +
                "2. 准备相关材料（成绩单、获奖证书等）\n" +
                "3. 在指定时间内提交申请\n" +
                "4. 等待评审结果\n" +
                "5. 获奖学生需参加颁奖仪式");

        FAQS.put("如何办理图书借阅", "图书借阅流程：\n" +
                "1. 携带学生证到图书馆\n" +
                "2. 在图书馆系统查询所需图书\n" +
                "3. 找到图书后到借阅处办理借阅手续\n" +
                "4. 借阅期限为30天，可续借一次\n" +
                "5. 逾期未还需缴纳滞纳金");

        // 初始化通用知识数据
        GENERAL_KNOWLEDGE.put("图书馆开放时间", "图书馆开放时间：\n" +
                "周一至周五：8:00-22:00\n" +
                "周六、周日：9:00-21:00\n" +
                "节假日开放时间另行通知");

        GENERAL_KNOWLEDGE.put("食堂营业时间", "食堂营业时间：\n" +
                "早餐：7:00-9:00\n" +
                "午餐：11:00-13:30\n" +
                "晚餐：17:00-19:30\n" +
                "各食堂具体时间可能略有不同");

        GENERAL_KNOWLEDGE.put("校园网使用", "校园网使用说明：\n" +
                "1. 学生账号为学号，初始密码为身份证后6位\n" +
                "2. 每月免费流量为20GB\n" +
                "3. 超出部分按0.1元/GB收费\n" +
                "4. 可在信息中心办理流量包\n" +
                "5. 遇到问题可联系信息中心：010-12345678");
    }

    @Override
    public String apply(KnowledgeRequest request, ToolContext toolContext) {
        if (request.operation == null || request.operation.trim().isEmpty()) {
            return "错误：必须指定operation参数";
        }

        try {
            return switch (request.operation.toLowerCase()) {
                case "queryregulation" -> queryRegulation(request.topic);
                case "queryfaq" -> queryFAQ(request.question);
                case "querygeneralknowledge" -> queryGeneralKnowledge(request.question);
                default ->
                        "错误：未知操作。支持的操作：queryRegulation（查询规章制度）、queryFAQ（解答常见问题）、queryGeneralKnowledge（解答通用知识）";
            };
        } catch (Exception e) {
            return "错误：" + e.getMessage();
        }
    }

    /**
     * 查询校园规章制度
     *
     * @param topic 规章制度主题
     * @return 格式化的规章制度信息
     */
    private String queryRegulation(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            return "错误：查询规章制度操作必须指定topic参数。可用主题：" + String.join("、", REGULATIONS.keySet());
        }

        // 先尝试精确匹配
        String regulation = REGULATIONS.get(topic);
        if (regulation != null) {
            return regulation;
        }

        // 尝试模糊匹配
        for (String key : REGULATIONS.keySet()) {
            if (key.contains(topic) || topic.contains(key)) {
                return REGULATIONS.get(key);
            }
        }

        return "未找到主题为" + topic + "的规章制度。可用主题：" + String.join("、", REGULATIONS.keySet());
    }

    /**
     * 解答校园常见问题
     *
     * @param question 问题内容
     * @return 格式化的问题解答
     */
    private String queryFAQ(String question) {
        if (question == null || question.trim().isEmpty()) {
            return "错误：解答常见问题操作必须指定question参数";
        }

        // 尝试匹配相关问题
        for (String key : FAQS.keySet()) {
            if (question.contains(key) || key.contains(question)) {
                return FAQS.get(key);
            }
        }

        // 无匹配时返回可用问题列表
        return "未找到关于\"" + question + "\"的解答。可用常见问题：" + String.join("、", FAQS.keySet());
    }

    /**
     * 解答校园通用知识问题
     *
     * @param question 问题内容
     * @return 格式化的知识解答
     */
    private String queryGeneralKnowledge(String question) {
        if (question == null || question.trim().isEmpty()) {
            return "错误：解答通用知识操作必须指定question参数";
        }

        // 尝试匹配相关知识
        for (String key : GENERAL_KNOWLEDGE.keySet()) {
            if (question.contains(key) || key.contains(question)) {
                return GENERAL_KNOWLEDGE.get(key);
            }
        }

        // 无匹配时返回可用知识主题
        return "未找到关于\"" + question + "\"的知识。可用主题：" + String.join("、", GENERAL_KNOWLEDGE.keySet());
    }

    /**
     * 校园知识查询请求参数类
     */
    public static class KnowledgeRequest {
        @JsonProperty(required = true)
        @JsonPropertyDescription("要执行的操作：queryRegulation（查询规章制度）、queryFAQ（解答常见问题）、queryGeneralKnowledge（解答通用知识）")
        public String operation;

        @JsonPropertyDescription("规章制度主题（例如：选课、考试、宿舍）")
        public String topic;

        @JsonPropertyDescription("问题内容（查询常见问题或通用知识时使用）")
        public String question;
    }
}