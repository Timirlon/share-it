package org.example.shareitgateway.bookings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.shareitserver.bookings.BookingFilteringState;
import org.example.shareitserver.bookings.dtos.BookingCreateDto;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import static org.example.shareitserver.utils.RequestConstants.USER_ID_REQUEST_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody BookingCreateDto bookingDto,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return bookingClient.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(
            @PathVariable int bookingId,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(name = "approved") boolean isApproved) {

        return bookingClient.updateStatus(bookingId, userId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(
            @PathVariable int bookingId,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return bookingClient.findById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findMyBookings(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(defaultValue = "ALL") BookingFilteringState state,
            @RequestParam(defaultValue = "0") @Min(value = 0) int from,
            @RequestParam(defaultValue = "10") @Range(min = 1, max = 20) int size) {

        return bookingClient.findMyBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingsOfMyItem(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(defaultValue = "ALL", required = false) BookingFilteringState state,
            @RequestParam(defaultValue = "0") @Min(value = 0) int from,
            @RequestParam(defaultValue = "10") @Range(min = 1, max = 20) int size) {

        return bookingClient.findBookingsOfMyItem(userId, state, from, size);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleHttpRequest(HttpClientErrorException ex) {
        return new ResponseEntity<>(
                ex.getResponseBodyAsString(),
                ex.getResponseHeaders(),
                ex.getStatusCode());
    }
}
