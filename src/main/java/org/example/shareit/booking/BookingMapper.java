package org.example.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.example.shareit.item.ItemMapper;
import org.example.shareit.user.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public BookingReadDto toReadDto(Booking booking) {
        BookingReadDto dto = new BookingReadDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStartDate().toString());
        dto.setEnd(booking.getEndDate().toString());
        dto.setItem(itemMapper.toDto(booking.getItem()));
        dto.setBooker(userMapper.toDto(booking.getBooker()));
        dto.setStatus(booking.getStatus());


        return dto;
    }

    public Booking fromCreateDto(BookingCreateDto dto) {
        Booking booking = new Booking();

        booking.setStartDate(dto.getStart());
        booking.setEndDate(dto.getEnd());

        return booking;
    }

    public List<BookingReadDto> toReadDto(Collection<Booking> bookings) {
        return bookings.stream()
                .map(this::toReadDto)
                .toList();
    }
}
