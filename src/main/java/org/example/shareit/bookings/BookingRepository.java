package org.example.shareit.bookings;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    // Получение списка всех бронирований текущего пользователя
    // ALL
    List<Booking> findAllByBooker_IdOrderByStartDateDesc(int bookerId, Pageable pageable);

    // CURRENT
    List<Booking> findAllByBooker_IdAndStartDateGreaterThanEqualAndEndDateIsAfterOrderByStartDateDesc(int bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // PAST
    List<Booking> findAllByBooker_IdAndEndDateIsBeforeOrderByStartDateDesc(int bookerId, LocalDateTime end, Pageable pageable);

    // FUTURE
    List<Booking> findAllByBooker_IdAndStartDateIsAfterOrderByStartDateDesc(int bookerId, LocalDateTime end, Pageable pageable);

    // WAITING/REJECTED - в зависимости от параметра
    List<Booking> findAllByBooker_IdAndStatusOrderByStartDateDesc(int bookerId, BookingStatus status, Pageable pageable);


    // Получение списка бронирований для всех вещей текущего пользователя
    // ALL
    List<Booking> findAllByItem_Owner_IdOrderByStartDateDesc(int ownerId, Pageable pageable);

    // CURRENT
    List<Booking> findAllByItem_Owner_IdAndStartDateGreaterThanEqualAndEndDateIsAfterOrderByStartDateDesc(int ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // PAST
    List<Booking> findAllByItem_Owner_IdAndEndDateIsBeforeOrderByStartDateDesc(int ownerId, LocalDateTime end, Pageable pageable);

    // FUTURE
    List<Booking> findAllByItem_Owner_IdAndStartDateIsAfterOrderByStartDateDesc(int ownerId, LocalDateTime end, Pageable pageable);

    // WAITING/REJECTED - в зависимости от параметра
    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(int ownerId, BookingStatus status, Pageable pageable);



    List<Booking> findAllByBooker_IdAndItem_IdAndStatusAndStartDateBeforeOrderByStartDateAsc(int bookerId, int itemId, BookingStatus status, LocalDateTime start);
}
