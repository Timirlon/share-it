package org.example.shareit.requests;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestReadDto {
    int id;
    String description;
    LocalDateTime created;
    List<ItemDto> responseItems;
}
