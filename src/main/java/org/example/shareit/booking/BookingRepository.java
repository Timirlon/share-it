package org.example.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBooker_IdOrderByEndDateDesc(int bookerId);

    List<Booking> findAllByItem_Owner_IdOrderByEndDateDesc(int ownerId);
}
