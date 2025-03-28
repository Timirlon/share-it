package org.example.shareit.bookings.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.shareit.bookings.BookingStatus;
import org.example.shareit.items.dtos.ItemReadDto;
import org.example.shareit.users.dtos.UserReadDto;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingReadDto {
    Integer id;
    String start;
    String end;
    ItemReadDto item;
    UserReadDto booker;
    BookingStatus status;
}
