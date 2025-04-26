package org.example.shareitserver.items.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.bookings.dtos.BookingBaseReadDto;
import org.example.shareitserver.bookings.dtos.BookingReadDto;
import org.example.shareitserver.items.comments.dtos.CommentReadDto;
import org.example.shareitserver.users.dtos.UserReadDto;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemReadDto {
    Integer id;

    String name;

    String description;

    Boolean available;

    List<CommentReadDto> comments = new ArrayList<>();

    UserReadDto owner;

    Integer requestId;

    BookingBaseReadDto lastBooking;

    BookingBaseReadDto nextBooking;
}
