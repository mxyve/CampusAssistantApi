package top.xym.campusassistantapi.module.agent.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.chat.model.ToolContext;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * 用于图书馆服务的工具类，包括图书查询、借阅记录和座位预约。
 *
 * @author moqi
 */
public class LibraryServiceTool implements BiFunction<LibraryServiceTool.LibraryRequest, ToolContext, String> {

    public static final String DESCRIPTION = """
            查询图书馆服务相关信息，包括图书查询、借阅记录和座位预约。
            
            支持的操作：
            - queryBook: 按书名、作者或ISBN查询图书
            - queryBorrowingRecord: 查询学生的图书借阅记录
            - querySeatReservation: 查询座位可用情况并进行预约
            
            使用示例：
            - 查询图书：operation="queryBook", keyword="Java编程"
            - 查询借阅记录：operation="queryBorrowingRecord", studentId="2022001"
            - 查询座位：operation="querySeatReservation", date="2025-09-15", timeSlot="14:00-16:00"
            """;

    // 模拟数据：图书馆数据库
    private static final Map<String, Book> BOOKS = new HashMap<>();
    // 模拟数据：借阅记录
    private static final Map<String, List<BorrowingRecord>> BORROWING_RECORDS = new HashMap<>();
    // 模拟数据：座位预约信息
    private static final Map<String, Map<String, SeatStatus>> SEAT_RESERVATIONS = new HashMap<>();

    static {
        // 初始化模拟图书数据
        BOOKS.put("ISBN001", new Book("ISBN001", "Java编程思想", "Bruce Eckel", "计算机", "可借阅", "3F-A-101"));
        BOOKS.put("ISBN002", new Book("ISBN002", "算法导论", "Thomas H. Cormen", "计算机", "已借出", "3F-A-102"));
        BOOKS.put("ISBN003", new Book("ISBN003", "深入理解计算机系统", "Randal E. Bryant", "计算机", "可借阅", "3F-A-103"));
        BOOKS.put("ISBN004", new Book("ISBN004", "设计模式", "Gang of Four", "计算机", "可借阅", "3F-A-104"));
        BOOKS.put("ISBN005", new Book("ISBN005", "高等数学", "同济大学", "数学", "可借阅", "2F-B-201"));

        // 初始化模拟借阅记录
        BORROWING_RECORDS.put("2022001", Arrays.asList(
                new BorrowingRecord("ISBN001", "Java编程思想", "2025-09-01", "2025-09-15", "借阅中"),
                new BorrowingRecord("ISBN002", "算法导论", "2025-09-10", "2025-09-24", "借阅中")
        ));
        BORROWING_RECORDS.put("2022002", Arrays.asList(
                new BorrowingRecord("ISBN003", "深入理解计算机系统", "2025-09-05", "2025-09-19", "借阅中")
        ));

        // 初始化模拟座位预约数据
        Map<String, SeatStatus> seats20250915 = new HashMap<>();
        seats20250915.put("14:00-16:00", new SeatStatus(50, 35, 15));
        seats20250915.put("16:00-18:00", new SeatStatus(50, 28, 22));
        SEAT_RESERVATIONS.put("2025-09-15", seats20250915);
    }

    @Override
    public String apply(LibraryRequest request, ToolContext toolContext) {
        if (request.operation == null || request.operation.trim().isEmpty()) {
            return "错误：必须指定operation参数";
        }

        try {
            return switch (request.operation.toLowerCase()) {
                case "querybook" -> queryBook(request.keyword);
                case "queryborrowingrecord" -> queryBorrowingRecord(request.studentId);
                case "queryseatreservation" -> querySeatReservation(request.date, request.timeSlot);
                default ->
                        "错误：未知操作。支持的操作：queryBook（查询图书）、queryBorrowingRecord（查询借阅记录）、querySeatReservation（查询座位预约）";
            };
        } catch (Exception e) {
            return "错误：" + e.getMessage();
        }
    }

    /**
     * 按关键词查询图书
     *
     * @param keyword 搜索关键词（书名/作者/ISBN）
     * @return 格式化的图书查询结果
     */
    private String queryBook(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return "错误：查询图书操作必须指定keyword参数";
        }

        List<Book> matchingBooks = BOOKS.values().stream()
                .filter(book -> book.title.contains(keyword) || book.author.contains(keyword) || book.isbn.equalsIgnoreCase(keyword))
                .collect(Collectors.toList());

        if (matchingBooks.isEmpty()) {
            return "未找到匹配关键词的图书：" + keyword;
        }

        StringBuilder result = new StringBuilder();
        result.append("图书搜索结果：\n\n");
        for (Book book : matchingBooks) {
            result.append(String.format("- %s\n", book.title));
            result.append(String.format("  作者：%s | ISBN：%s\n", book.author, book.isbn));
            result.append(String.format("  分类：%s | 状态：%s | 馆藏位置：%s\n\n", book.category, book.status, book.location));
        }
        return result.toString();
    }

    /**
     * 查询学生的图书借阅记录
     *
     * @param studentId 学生ID
     * @return 格式化的借阅记录信息
     */
    private String queryBorrowingRecord(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return "错误：查询借阅记录操作必须指定studentId参数";
        }

        List<BorrowingRecord> records = BORROWING_RECORDS.getOrDefault(studentId, Collections.emptyList());
        if (records.isEmpty()) {
            return "未找到学生" + studentId + "的借阅记录";
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("学生%s的借阅记录：\n\n", studentId));
        for (BorrowingRecord record : records) {
            result.append(String.format("- %s（ISBN：%s）\n", record.bookTitle, record.isbn));
            result.append(String.format("  借阅日期：%s | 归还日期：%s | 状态：%s\n\n", record.borrowDate, record.dueDate, record.status));
        }
        return result.toString();
    }

    /**
     * 查询图书馆座位预约信息
     *
     * @param date     日期（格式：YYYY-MM-DD）
     * @param timeSlot 时间段（例如：14:00-16:00）
     * @return 格式化的座位预约信息
     */
    private String querySeatReservation(String date, String timeSlot) {
        if (date == null || date.trim().isEmpty()) {
            return "错误：查询座位预约操作必须指定date参数";
        }

        Map<String, SeatStatus> seats = SEAT_RESERVATIONS.getOrDefault(date, new HashMap<>());
        if (seats.isEmpty()) {
            return "未找到" + date + "的座位信息";
        }

        if (timeSlot != null && !timeSlot.trim().isEmpty()) {
            SeatStatus status = seats.get(timeSlot);
            if (status == null) {
                return String.format("未找到%s %s时间段的座位信息", date, timeSlot);
            }
            return String.format("""
                            %s %s座位可用情况：
                            总座位数：%d
                            已预约：%d
                            可预约：%d""",
                    date, timeSlot, status.total, status.reserved, status.available);
        } else {
            StringBuilder result = new StringBuilder();
            result.append(String.format("%s座位可用情况：\n\n", date));
            for (Map.Entry<String, SeatStatus> entry : seats.entrySet()) {
                result.append(String.format("时间段：%s\n", entry.getKey()));
                result.append(String.format("  总数：%d | 已预约：%d | 可预约：%d\n\n",
                        entry.getValue().total, entry.getValue().reserved, entry.getValue().available));
            }
            return result.toString();
        }
    }

    /**
     * 图书馆服务查询请求参数类
     */
    public static class LibraryRequest {
        @JsonProperty(required = true)
        @JsonPropertyDescription("要执行的操作：queryBook（查询图书）、queryBorrowingRecord（查询借阅记录）、querySeatReservation（查询座位预约）")
        public String operation;

        @JsonPropertyDescription("搜索关键词（查询图书时使用）")
        public String keyword;

        @JsonPropertyDescription("学生ID（查询借阅记录时必填）")
        public String studentId;

        @JsonPropertyDescription("日期（格式：YYYY-MM-DD，查询座位预约时必填）")
        public String date;

        @JsonPropertyDescription("时间段（例如：14:00-16:00，查询座位预约时可选）")
        public String timeSlot;
    }

    /**
     * 图书实体类
     */
    private static class Book {
        String isbn;      // 图书ISBN
        String title;     // 书名
        String author;    // 作者
        String category;  // 分类
        String status;    // 状态（可借阅/已借出）
        String location;  // 馆藏位置

        Book(String isbn, String title, String author, String category, String status, String location) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.category = category;
            this.status = status;
            this.location = location;
        }
    }

    /**
     * 借阅记录实体类
     */
    private static class BorrowingRecord {
        String isbn;       // 图书ISBN
        String bookTitle;  // 书名
        String borrowDate; // 借阅日期
        String dueDate;    // 归还日期
        String status;     // 借阅状态

        BorrowingRecord(String isbn, String bookTitle, String borrowDate, String dueDate, String status) {
            this.isbn = isbn;
            this.bookTitle = bookTitle;
            this.borrowDate = borrowDate;
            this.dueDate = dueDate;
            this.status = status;
        }
    }

    /**
     * 座位状态实体类
     */
    private static class SeatStatus {
        int total;      // 总座位数
        int reserved;   // 已预约数
        int available;  // 可预约数

        SeatStatus(int total, int reserved, int available) {
            this.total = total;
            this.reserved = reserved;
            this.available = available;
        }
    }
}