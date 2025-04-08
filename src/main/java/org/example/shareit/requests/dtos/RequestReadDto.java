package org.example.shareit.requests.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.shareit.items.dtos.ItemReadDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestReadDto {
    Integer id;
    String description;
    LocalDateTime created;
    List<ItemReadDto> responseItems;
}
