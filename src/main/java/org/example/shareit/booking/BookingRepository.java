package org.example.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    // Получение списка всех бронирований текущего пользователя
    // ALL
    List<Booking> findAllByBooker_IdOrderByStartDateDesc(int bookerId);

    // CURRENT
    List<Booking> findAllByBooker_IdAndStartDateGreaterThanEqualAndEndDateIsAfterOrderByStartDateDesc(int bookerId, LocalDateTime start, LocalDateTime end);

    // PAST
    List<Booking> findAllByBooker_IdAndEndDateIsBeforeOrderByStartDateDesc(int bookerId, LocalDateTime end);

    // FUTURE
    List<Booking> findAllByBooker_IdAndStartDateIsAfterOrderByStartDateDesc(int bookerId, LocalDateTime end);

    // WAITING/REJECTED - в зависимости от параметра
    List<Booking> findAllByBooker_IdAndStatusOrderByStartDateDesc(int bookerId, BookingStatus status);


    // Получение списка бронирований для всех вещей текущего пользователя
    // ALL
    List<Booking> findAllByItem_Owner_IdOrderByStartDateDesc(int ownerId);

    // CURRENT
    List<Booking> findAllByItem_Owner_IdAndStartDateGreaterThanEqualAndEndDateIsAfterOrderByStartDateDesc(int ownerId, LocalDateTime start, LocalDateTime end);

    // PAST
    List<Booking> findAllByItem_Owner_IdAndEndDateIsBeforeOrderByStartDateDesc(int ownerId, LocalDateTime end);

    // FUTURE
    List<Booking> findAllByItem_Owner_IdAndStartDateIsAfterOrderByStartDateDesc(int ownerId, LocalDateTime end);

    // WAITING/REJECTED - в зависимости от параметра
    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(int ownerId, BookingStatus status);


    List<Booking> findAllByBooker_IdAndItem_Id(int bookerId, int itemId);
}
