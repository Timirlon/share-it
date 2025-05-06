package org.example.shareitserver.bookings;

import org.example.shareitserver.exceptions.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.exceptions.NotFoundException;
import org.example.shareitserver.items.Item;
import org.example.shareitserver.items.ItemRepository;
import org.example.shareitserver.users.User;
import org.example.shareitserver.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@Service
public class BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    public Booking create(Booking booking, int userId, int bookedItemId) {
        User booker = findUserByIdOrElseThrowNotFound(userId);
        booking.setBooker(booker);

        Item bookedItem = findItemByIdOrElseThrowNotFound(bookedItemId);


        if (booking.getEndDate().isBefore(booking.getStartDate())
        || booking.getEndDate().isEqual(booking.getStartDate())) {
            throw new ValidationException("Некорректная дата или время окончания брони.");
        }

        booking.setStatus(BookingStatus.WAITING);


        if (userId == bookedItem.getOwner().getId()) {
            throw new NotFoundException("Товар не найден.");
        }

        if (!bookedItem.getAvailable()) {
            throw new ValidationException("Товар недоступен.");
        }
        booking.setItem(bookedItem);


        bookingRepository.save(booking);
        return booking;
    }

    public Booking updateStatus(int bookingId, int userId, boolean isApproved) {
        findUserByIdOrElseThrowNotFound(userId);

        Booking booking = findBookingByIdOrElseThrowNotFound(bookingId);


        if (userId != booking.getItem().getOwner().getId()) {
            throw new NotFoundException("Запись не найдена.");
        }

        if (isApproved && booking.getStatus() == BookingStatus.APPROVED
        || !isApproved && booking.getStatus() == BookingStatus.REJECTED) {
            throw new ValidationException("Статусу записи уже задано данное значение.");
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
        findUserByIdOrElseThrowNotFound(userId);

        Booking booking = findBookingByIdOrElseThrowNotFound(bookingId);

        if (userId != booking.getBooker().getId()
                && userId != booking.getItem().getOwner().getId()) {

            throw new NotFoundException("Запись не найдена.");
        }


        return booking;
    }

    public Page<Booking> findAllByBookerId(int bookerId, BookingFilteringState state, int from, int size) {
        findUserByIdOrElseThrowNotFound(bookerId);


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

    public Page<Booking> findAllByItemOwnerId(int ownerId, BookingFilteringState state, int from, int size) {
        findUserByIdOrElseThrowNotFound(ownerId);


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

    private User findUserByIdOrElseThrowNotFound(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    private Booking findBookingByIdOrElseThrowNotFound(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Запись не найдена."));
    }

    private Item findItemByIdOrElseThrowNotFound(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));
    }
}
