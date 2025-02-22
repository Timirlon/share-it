package org.example.shareit.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.shareit.user.User;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    int id;
    String name;
    String description;
    boolean available;
    User owner;
}
