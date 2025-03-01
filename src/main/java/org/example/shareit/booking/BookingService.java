package org.example.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.example.shareit.exception.NotFoundException;
import org.example.shareit.item.Item;
import org.example.shareit.item.ItemRepository;
import org.example.shareit.user.User;
import org.example.shareit.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

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
        booking.setItem(bookedItem);


        return mapper.toReadDto(bookingRepository.save(booking));
    }

    public BookingReadDto approve(int bookingId, int userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Запись не найдена."));

        if (userId != booking.getItem().getOwner().getId()) {
            throw new NotFoundException("Запись не найдена.");
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
                || userId != booking.getItem().getOwner().getId()) {

            throw new NotFoundException("Запись не найдена.");
        }

        return mapper.toReadDto(booking);
    }

    public List<BookingReadDto> findAllByBookerId(int userId, FilterState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBooker_IdOrderByStartDateDesc(userId);
            case CURRENT ->
                    bookingRepository.findAllByBooker_IdAndStartDateGreaterThanEqualAndEndDateIsAfterOrderByStartDateDesc(
                            userId, now, now);
            case PAST ->
                    bookingRepository.findAllByBooker_IdAndEndDateIsBeforeOrderByStartDateDesc(userId, now);
            case FUTURE ->
                    bookingRepository.findAllByBooker_IdAndStartDateIsAfterOrderByStartDateDesc(userId, now);
            case WAITING ->
                    bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(userId, BookingStatus.REJECTED);
        };


        return mapper.toReadDto(bookings);
    }

    public List<BookingReadDto> findAllByItemOwnerId(int ownerId, FilterState state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItem_Owner_IdOrderByStartDateDesc(ownerId);
            case CURRENT ->
                    bookingRepository.findAllByItem_Owner_IdAndStartDateGreaterThanEqualAndEndDateIsAfterOrderByStartDateDesc(
                            ownerId, now, now);
            case FUTURE ->
                    bookingRepository.findAllByItem_Owner_IdAndStartDateIsAfterOrderByStartDateDesc(ownerId, now);
            case PAST ->
                    bookingRepository.findAllByItem_Owner_IdAndEndDateIsBeforeOrderByStartDateDesc(ownerId, now);
            case WAITING ->
                    bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.REJECTED);
        };

        return mapper.toReadDto(bookings);
    }

    private List<BookingReadDto> filterBookingsByState(List<Booking> bookings, String state) {
        Stream<Booking> stream = bookings.stream();
        LocalDateTime now = LocalDateTime.now();


        if (state.equalsIgnoreCase("CURRENT")) {

            stream = stream.filter(booking -> booking.getStartDate().isBefore(now)
                    && booking.getEndDate().isAfter(now)
                    && booking.getStatus() == BookingStatus.APPROVED);

        } else if (state.equalsIgnoreCase("PAST")) {

            stream = stream.filter(booking -> booking.getEndDate().isBefore(now)
                    && booking.getStatus() == BookingStatus.APPROVED);

        } else if (state.equalsIgnoreCase("FUTURE")) {

            stream = stream.filter(booking -> booking.getStartDate().isAfter(now)
                    && booking.getStatus() == BookingStatus.APPROVED);

        } else if (state.equalsIgnoreCase("WAITING")) {

            stream = stream.filter(booking -> booking.getStatus() == BookingStatus.WAITING);

        } else if (state.equalsIgnoreCase("REJECTED")) {

            stream = stream.filter(booking -> booking.getStatus() == BookingStatus.REJECTED);

        } else if (!state.equalsIgnoreCase("ALL")) {

            throw new IllegalArgumentException();
        }


        return stream.map(mapper::toReadDto).toList();
    }
}
