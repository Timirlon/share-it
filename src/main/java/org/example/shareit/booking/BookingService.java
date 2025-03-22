package org.example.shareit.booking;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.example.shareit.exception.ForbiddenAccessException;
import org.example.shareit.exception.NotFoundException;
import org.example.shareit.item.Item;
import org.example.shareit.item.ItemRepository;
import org.example.shareit.user.User;
import org.example.shareit.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingReadDto create(BookingCreateDto dto, int userId) {
        Booking booking = mapper.fromCreateDto(dto);
        booking.setStatus(BookingStatus.WAITING);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        booking.setBooker(booker);

        Item bookedItem = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Товар не найден."));

        if (!bookedItem.isAvailable()) {
            throw new ValidationException("Товар недоступен.");
        }
        booking.setItem(bookedItem);


        return mapper.toReadDto(bookingRepository.save(booking));
    }

    public BookingReadDto approve(int bookingId, int userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Запись не найдена."));


        if (userId != booking.getItem().getOwner().getId()) {
            throw new ForbiddenAccessException("Вы не можете одобрить запрос.");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }


        return mapper.toReadDto(bookingRepository.save(booking));
    }

    public BookingReadDto findById(int bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Запись не найдена."));

        if (userId != booking.getBooker().getId()
                && userId != booking.getItem().getOwner().getId()) {

            throw new NotFoundException("Запись не найдена.");
        }

        return mapper.toReadDto(booking);
    }

    public List<BookingReadDto> findAllByBookerId(int userId, FilterState state, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));


        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);


        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBooker_IdOrderByStartDateDesc(userId, pageable);
            case CURRENT ->
                    bookingRepository.findAllByBooker_IdAndStartDateGreaterThanEqualAndEndDateIsAfterOrderByStartDateDesc(
                            userId, now, now, pageable);
            case PAST ->
                    bookingRepository.findAllByBooker_IdAndEndDateIsBeforeOrderByStartDateDesc(userId, now, pageable);
            case FUTURE ->
                    bookingRepository.findAllByBooker_IdAndStartDateIsAfterOrderByStartDateDesc(userId, now, pageable);
            case WAITING ->
                    bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(userId, BookingStatus.WAITING, pageable);
            case REJECTED ->
                    bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(userId, BookingStatus.REJECTED, pageable);
        };


        return mapper.toReadDto(bookings);
    }

    public List<BookingReadDto> findAllByItemOwnerId(int ownerId, FilterState state, int from, int size) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));


        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);


        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItem_Owner_IdOrderByStartDateDesc(ownerId, pageable);
            case CURRENT ->
                    bookingRepository.findAllByItem_Owner_IdAndStartDateGreaterThanEqualAndEndDateIsAfterOrderByStartDateDesc(
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

        return mapper.toReadDto(bookings);
    }

}
