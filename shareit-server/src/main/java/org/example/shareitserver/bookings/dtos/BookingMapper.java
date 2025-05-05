package org.example.shareitserver.bookings.dtos;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.bookings.Booking;
import org.example.shareitserver.items.dtos.ItemMapper;
import org.example.shareitserver.users.dtos.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@Component
@RequiredArgsConstructor
public class BookingMapper {
    UserMapper userMapper;
    ItemMapper itemMapper;

    public BookingReadDto toDto(Booking booking) {
        BookingReadDto dto = new BookingReadDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStartDate());
        dto.setEnd(booking.getEndDate());
        dto.setBooker(userMapper.toDto(booking.getBooker()));
        dto.setStatus(booking.getStatus());
        dto.setItem(itemMapper.toDto(booking.getItem()));


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
