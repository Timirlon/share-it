package org.example.shareit.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentReadDto {
    Integer id;
    String text;
    String itemName;
    String authorName;
    LocalDateTime created;
}
