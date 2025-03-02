package org.example.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            @RequestParam(defaultValue = "ALL", required = false) FilterState state) {

        return bookingService.findAllByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingReadDto> findBookingsOfMyItem(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(defaultValue = "ALL", required = false) FilterState state) {

        return bookingService.findAllByItemOwnerId(userId, state);
    }
}
