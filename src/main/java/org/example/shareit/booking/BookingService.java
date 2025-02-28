package org.example.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.example.shareit.exception.NotFoundException;
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

    public BookingReadDto create(BookingCreateDto dto, int userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        Booking booking = mapper.fromCreateDto(dto);
        booking.setBooker(booker);


        return mapper.toReadDto(bookingRepository.save(booking));
    }

    public BookingReadDto approve(int bookingId, int userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Запись не найдена."));

        if (userId != booking.getBooker().getId()) {
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

    public List<BookingReadDto> findAllByBookerId(int userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        List<Booking> allBookings = bookingRepository.findAllByBooker_IdOrderByEndDateDesc(userId);
        return filterBookingsByState(allBookings, state);
    }

    public List<BookingReadDto> findAllByItemOwnerId(int ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        List<Booking> allBookings = bookingRepository.findAllByBooker_IdOrderByEndDateDesc(ownerId);
        return filterBookingsByState(allBookings, state);
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
