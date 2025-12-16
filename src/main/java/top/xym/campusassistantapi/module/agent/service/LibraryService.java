package top.xym.campusassistantapi.module.agent.service;

import top.xym.campusassistantapi.module.agent.model.entity.Book;
import top.xym.campusassistantapi.module.agent.model.entity.BorrowingRecord;
import top.xym.campusassistantapi.module.agent.model.entity.SeatReservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LibraryService {
    /**
     * 搜索图书
     */
    List<Book> searchBooks(String keyword);

    /**
     * 查询借阅记录
     */
    List<BorrowingRecord> getBorrowingRecords(String studentId);

    /**
     * 查询座位可用性
     */
    Map<String, SeatReservation> getSeatReservations(LocalDate date, String timeSlot);
}

