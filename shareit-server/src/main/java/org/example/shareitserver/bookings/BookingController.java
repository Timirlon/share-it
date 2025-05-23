package org.example.shareitserver.bookings;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.bookings.dtos.BookingCreateDto;
import org.example.shareitserver.bookings.dtos.BookingMapper;
import org.example.shareitserver.bookings.dtos.BookingReadDto;
import org.example.shareitserver.exceptions.UnsupportedStateException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.shareitserver.utils.RequestConstants.USER_ID_REQUEST_HEADER;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@RestController
@RequestMapping("/bookings")
public class BookingController {
    BookingService bookingService;
    BookingMapper bookingMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingReadDto create(
            @RequestBody BookingCreateDto bookingDto,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        Booking booking = bookingMapper.fromDto(bookingDto);
        int itemId = bookingDto.getItemId();

        return bookingMapper.toDto(
                bookingService.create(booking, userId, itemId));
    }

    @PatchMapping("/{bookingId}")
    public BookingReadDto updateStatus(
            @PathVariable int bookingId,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(name = "approved") boolean isApproved) {

        return bookingMapper.toDto(
                bookingService.updateStatus(bookingId, userId, isApproved));
    }

    @GetMapping("/{bookingId}")
    public BookingReadDto findById(
            @PathVariable int bookingId,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return bookingMapper.toDto(
                bookingService.findById(bookingId, userId));
    }

    @GetMapping
    public List<BookingReadDto> findMyBookings(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        BookingFilteringState enumState = getFilteringState(state);


        return bookingMapper.toDto(
                bookingService.findAllByBookerId(userId, enumState, from, size));
    }

    @GetMapping("/owner")
    public List<BookingReadDto> findBookingsOfMyItem(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        BookingFilteringState enumState = getFilteringState(state);

        return bookingMapper.toDto(
                bookingService.findAllByItemOwnerId(userId, enumState, from, size));
    }

    private BookingFilteringState getFilteringState(String strState) {
        if (strState.equals(BookingFilteringState.ALL.toString())) {
            return BookingFilteringState.ALL;
        } else if (strState.equals(BookingFilteringState.CURRENT.toString())) {
            return BookingFilteringState.CURRENT;
        } else if (strState.equals(BookingFilteringState.PAST.toString())) {
            return BookingFilteringState.PAST;
        } else if (strState.equals(BookingFilteringState.FUTURE.toString())) {
            return BookingFilteringState.FUTURE;
        } else if (strState.equals(BookingFilteringState.WAITING.toString())) {
            return BookingFilteringState.WAITING;
        } else if (strState.equals(BookingFilteringState.REJECTED.toString())) {
            return BookingFilteringState.REJECTED;
        } else {
            throw new UnsupportedStateException("Некорректное состояние для фильтрации записей");
        }
    }
}
