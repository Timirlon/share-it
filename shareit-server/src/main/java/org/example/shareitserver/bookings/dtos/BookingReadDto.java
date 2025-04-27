package org.example.shareitserver.bookings.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.bookings.BookingStatus;
import org.example.shareitserver.items.dtos.ItemReadDto;
import org.example.shareitserver.users.dtos.UserReadDto;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingReadDto {
    Integer id;
    LocalDateTime start;
    LocalDateTime end;
    ItemReadDto item;
    UserReadDto booker;
    BookingStatus status;
}
