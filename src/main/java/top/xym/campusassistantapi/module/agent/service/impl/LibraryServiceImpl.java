package top.xym.campusassistantapi.module.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.xym.campusassistantapi.module.agent.model.entity.Book;
import top.xym.campusassistantapi.module.agent.model.entity.BorrowingRecord;
import top.xym.campusassistantapi.module.agent.model.entity.SeatReservation;
import top.xym.campusassistantapi.module.agent.mapper.BookMapper;
import top.xym.campusassistantapi.module.agent.mapper.BorrowingRecordMapper;
import top.xym.campusassistantapi.module.agent.mapper.SeatReservationMapper;
import top.xym.campusassistantapi.module.agent.service.LibraryService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 图书馆服务实现类
 *
 * @author moqi
 */
@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {

    private final BookMapper bookMapper;
    private final BorrowingRecordMapper borrowingRecordMapper;
    private final SeatReservationMapper seatReservationMapper;


    @Override
    public List<Book> searchBooks(String keyword) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(Book::getTitle, keyword).or().like(Book::getAuthor, keyword).or().eq(Book::getIsbn, keyword));
        return bookMapper.selectList(wrapper);
    }

    @Override
    public List<BorrowingRecord> getBorrowingRecords(String studentId) {
        LambdaQueryWrapper<BorrowingRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowingRecord::getStudentId, studentId);
        return borrowingRecordMapper.selectList(wrapper);
    }

    @Override
    public Map<String, SeatReservation> getSeatReservations(LocalDate date, String timeSlot) {
        LambdaQueryWrapper<SeatReservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeatReservation::getDate, date);
        if (timeSlot != null && !timeSlot.trim().isEmpty()) {
            wrapper.eq(SeatReservation::getTimeSlot, timeSlot);
        }
        List<SeatReservation> reservations = seatReservationMapper.selectList(wrapper);
        return reservations.stream().collect(Collectors.toMap(SeatReservation::getTimeSlot, r -> r, (k1, k2) -> k1));
    }
}

