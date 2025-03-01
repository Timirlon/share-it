package org.example.shareit.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCreateDto {
    @NotNull
    @FutureOrPresent
    LocalDateTime start;

    @NotNull
    @Future
    LocalDateTime end;

    @NotNull
    Integer itemId;
}
