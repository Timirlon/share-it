package org.example.shareit.bookings.dtos;

import lombok.RequiredArgsConstructor;
import org.example.shareit.bookings.Booking;
import org.example.shareit.items.dtos.ItemMapper;
import org.example.shareit.users.dtos.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public BookingReadDto toDto(Booking booking) {
        BookingReadDto dto = new BookingReadDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStartDate().toString());
        dto.setEnd(booking.getEndDate().toString());
        dto.setItem(itemMapper.toDto(booking.getItem()));
        dto.setBooker(userMapper.toDto(booking.getBooker()));
        dto.setStatus(booking.getStatus());


        return dto;
    }

    public Booking fromDto(BookingCreateDto dto) {
        Booking booking = new Booking();

        booking.setStartDate(dto.getStart());
        booking.setEndDate(dto.getEnd());

        return booking;
    }

    public List<BookingReadDto> toDto(Collection<Booking> bookings) {
        return bookings.stream()
                .map(this::toDto)
                .toList();
    }
}
