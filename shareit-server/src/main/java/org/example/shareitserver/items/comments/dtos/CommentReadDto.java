package org.example.shareitserver.items.comments.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentReadDto {
    Integer id;
    String text;
    String itemName;
    String authorName;
    LocalDateTime created;
}
