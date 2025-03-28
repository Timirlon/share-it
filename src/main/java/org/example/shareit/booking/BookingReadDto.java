package org.example.shareit.booking;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.shareit.item.ItemDto;
import org.example.shareit.user.UserDto;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingReadDto {
    Integer id;
    String start;
    String end;
    ItemDto item;
    UserDto booker;
    BookingStatus status;
}
