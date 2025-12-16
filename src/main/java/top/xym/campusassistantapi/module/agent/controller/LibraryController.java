package top.xym.campusassistantapi.module.agent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.xym.campusassistantapi.module.agent.model.entity.Book;
import top.xym.campusassistantapi.module.agent.model.entity.BorrowingRecord;
import top.xym.campusassistantapi.module.agent.model.entity.SeatReservation;
import top.xym.campusassistantapi.module.agent.service.LibraryService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 图书馆控制器
 *
 * @author moqi
 */
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@Tag(name = "图书馆相关", description = "图书馆相关接口")
public class LibraryController {

    private final LibraryService libraryService;

    /**
     * 搜索图书
     */
    @GetMapping("/book/search")
    @Operation(summary = "搜索图书")
    public List<Book> searchBooks(@RequestParam String keyword) {
        return libraryService.searchBooks(keyword);
    }

    /**
     * 查询借阅记录
     */
    @GetMapping("/borrowing")
    @Operation(summary = "查询借阅记录")
    public List<BorrowingRecord> getBorrowingRecords(@RequestParam String studentId) {
        return libraryService.getBorrowingRecords(studentId);
    }

    /**
     * 查询座位可用性
     */
    @GetMapping("/seat")
    @Operation(summary = "查询座位可用性")
    public Map<String, SeatReservation> getSeatReservations(@RequestParam String date,
                                                            @RequestParam(required = false) String timeSlot) {
        LocalDate localDate = LocalDate.parse(date);
        return libraryService.getSeatReservations(localDate, timeSlot);
    }
}