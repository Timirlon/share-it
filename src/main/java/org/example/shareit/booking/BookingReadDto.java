package org.example.shareit.booking;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingReadDto {
    int id;
    String startDate;
    String endDate;
    String itemName;
    String bookerName;
    String status;
}
