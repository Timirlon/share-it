package org.example.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.shareit.utils.RequestConstants.USER_ID_REQUEST_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingReadDto create(
            @Valid @RequestBody BookingCreateDto booking,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingReadDto approve(
            @PathVariable int bookingId,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam boolean approved) {

        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingReadDto findById(
            @PathVariable int bookingId,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingReadDto> findMyBookings(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(defaultValue = "ALL") FilterState state,
            @RequestParam(defaultValue = "0") @Min(value = 0) int from,
            @RequestParam(defaultValue = "10") @Range(min = 1, max = 20) int size) {

        return bookingService.findAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingReadDto> findBookingsOfMyItem(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(defaultValue = "ALL", required = false) FilterState state,
            @RequestParam(defaultValue = "0") @Min(value = 0) int from,
            @RequestParam(defaultValue = "10") @Range(min = 1, max = 20) int size) {

        return bookingService.findAllByItemOwnerId(userId, state, from, size);
    }
}
