package org.example.shareitserver.bookings.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.bookings.BookingStatus;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingBaseReadDto {
    Integer id;
    LocalDateTime start;
    LocalDateTime end;
    int bookerId;
    String bookerName;
    BookingStatus status;
}
