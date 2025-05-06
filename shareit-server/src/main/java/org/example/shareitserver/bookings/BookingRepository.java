package org.example.shareitserver.bookings;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    // Получение списка всех бронирований текущего пользователя
    // ALL
    Page<Booking> findAllByBooker_IdOrderByStartDateDesc(int bookerId, Pageable pageable);

    // CURRENT
    Page<Booking> findAllByBooker_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(int bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // PAST
    Page<Booking> findAllByBooker_IdAndEndDateIsBeforeOrderByStartDateDesc(int bookerId, LocalDateTime end, Pageable pageable);

    // FUTURE
    Page<Booking> findAllByBooker_IdAndStartDateIsAfterOrderByStartDateDesc(int bookerId, LocalDateTime end, Pageable pageable);

    // WAITING/REJECTED - в зависимости от параметра
    Page<Booking> findAllByBooker_IdAndStatusOrderByStartDateDesc(int bookerId, BookingStatus status, Pageable pageable);


    // Получение списка бронирований для всех вещей текущего пользователя
    // ALL
    Page<Booking> findAllByItem_Owner_IdOrderByStartDateDesc(int ownerId, Pageable pageable);

    // CURRENT
    Page<Booking> findAllByItem_Owner_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(int ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // PAST
    Page<Booking> findAllByItem_Owner_IdAndEndDateIsBeforeOrderByStartDateDesc(int ownerId, LocalDateTime end, Pageable pageable);

    // FUTURE
    Page<Booking> findAllByItem_Owner_IdAndStartDateIsAfterOrderByStartDateDesc(int ownerId, LocalDateTime end, Pageable pageable);

    // WAITING/REJECTED - в зависимости от параметра
    Page<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(int ownerId, BookingStatus status, Pageable pageable);


    List<Booking> findAllByBooker_IdAndItem_IdAndStatusAndStartDateBeforeOrderByStartDateAsc(int bookerId, int itemId, BookingStatus status, LocalDateTime start);


    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id = :itemId
            AND b.status = :status
            AND b.startDate < :time
            ORDER BY b.endDate DESC
            LIMIT 1""")
    Optional<Booking> findLastBooking(int itemId, BookingStatus status, LocalDateTime time);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id = :itemId
            AND b.status = :status
            AND b.startDate > :time
            ORDER BY b.startDate
            LIMIT 1""")
    Optional<Booking> findNextBooking(int itemId, BookingStatus status, LocalDateTime time);
}
