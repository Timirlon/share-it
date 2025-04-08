package org.example.shareit.bookings;

import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareit.exceptions.ForbiddenAccessException;
import org.example.shareit.exceptions.NotFoundException;
import org.example.shareit.items.Item;
import org.example.shareit.items.ItemRepository;
import org.example.shareit.users.User;
import org.example.shareit.users.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@Service
public class BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    public Booking create(Booking booking, int userId, int bookedItemId) {
        booking.setStatus(BookingStatus.WAITING);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        booking.setBooker(booker);

        Item bookedItem = itemRepository.findById(bookedItemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));

        if (!bookedItem.getAvailable()) {
            throw new ValidationException("Товар недоступен.");
        }
        booking.setItem(bookedItem);


        bookingRepository.save(booking);
        return booking;
    }

    public Booking updateStatus(int bookingId, int userId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Запись не найдена."));


        if (userId != booking.getItem().getOwner().getId()) {
            throw new ForbiddenAccessException("Вы не можете одобрить запрос.");
        }

        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }


        bookingRepository.save(booking);
        return booking;
    }

    public Booking findById(int bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Запись не найдена."));

        if (userId != booking.getBooker().getId()
                && userId != booking.getItem().getOwner().getId()) {

            throw new NotFoundException("Запись не найдена.");
        }


        return booking;
    }

    public List<Booking> findAllByBookerId(int bookerId, BookingFilteringState state, int from, int size) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));


        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);


        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case ALL -> bookingRepository.findAllByBooker_IdOrderByStartDateDesc(bookerId, pageable);
            case CURRENT ->
                    bookingRepository.findAllByBooker_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(
                            bookerId, now, now, pageable);
            case PAST ->
                    bookingRepository.findAllByBooker_IdAndEndDateIsBeforeOrderByStartDateDesc(bookerId, now, pageable);
            case FUTURE ->
                    bookingRepository.findAllByBooker_IdAndStartDateIsAfterOrderByStartDateDesc(bookerId, now, pageable);
            case WAITING ->
                    bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.WAITING, pageable);
            case REJECTED ->
                    bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.REJECTED, pageable);
        };
    }

    public List<Booking> findAllByItemOwnerId(int ownerId, BookingFilteringState state, int from, int size) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));


        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);


        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case ALL -> bookingRepository.findAllByItem_Owner_IdOrderByStartDateDesc(ownerId, pageable);
            case CURRENT ->
                    bookingRepository.findAllByItem_Owner_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(
                            ownerId, now, now, pageable);
            case FUTURE ->
                    bookingRepository.findAllByItem_Owner_IdAndStartDateIsAfterOrderByStartDateDesc(ownerId, now, pageable);
            case PAST ->
                    bookingRepository.findAllByItem_Owner_IdAndEndDateIsBeforeOrderByStartDateDesc(ownerId, now, pageable);
            case WAITING ->
                    bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.WAITING, pageable);
            case REJECTED ->
                    bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.REJECTED, pageable);
        };
    }

}
