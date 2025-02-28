package org.example.shareit.booking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BookingStatus {
    WAITING("На рассмотрении"),
    APPROVED("Одобрено"),
    REJECTED("Отклонено");

    private final String displayName;
}
