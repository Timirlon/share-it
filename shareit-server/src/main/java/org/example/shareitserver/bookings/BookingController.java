package org.example.shareitserver.bookings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.bookings.dtos.BookingCreateDto;
import org.example.shareitserver.bookings.dtos.BookingMapper;
import org.example.shareitserver.bookings.dtos.BookingReadDto;
import org.hibernate.validator.constraints.Range;
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
    BookingMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingReadDto create(
            @Valid @RequestBody BookingCreateDto bookingDto,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        Booking booking = mapper.fromDto(bookingDto);
        int itemId = bookingDto.getItemId();

        return mapper.toDto(
                bookingService.create(booking, userId, itemId));
    }

    @PatchMapping("/{bookingId}")
    public BookingReadDto updateStatus(
            @PathVariable int bookingId,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(name = "approved") boolean isApproved) {

        return mapper.toDto(
                bookingService.updateStatus(bookingId, userId, isApproved));
    }

    @GetMapping("/{bookingId}")
    public BookingReadDto findById(
            @PathVariable int bookingId,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return mapper.toDto(
                bookingService.findById(bookingId, userId));
    }

    @GetMapping
    public List<BookingReadDto> findMyBookings(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(defaultValue = "ALL") BookingFilteringState state,
            @RequestParam(defaultValue = "0") @Min(value = 0) int from,
            @RequestParam(defaultValue = "10") @Range(min = 1, max = 20) int size) {

        return mapper.toDto(
                bookingService.findAllByBookerId(userId, state, from, size));
    }

    @GetMapping("/owner")
    public List<BookingReadDto> findBookingsOfMyItem(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(defaultValue = "ALL", required = false) BookingFilteringState state,
            @RequestParam(defaultValue = "0") @Min(value = 0) int from,
            @RequestParam(defaultValue = "10") @Range(min = 1, max = 20) int size) {

        return mapper.toDto(
                bookingService.findAllByItemOwnerId(userId, state, from, size));
    }
}
