package org.example.shareit.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    int id;
    String name;
    String description;
    Boolean available;
    String owner;
}
