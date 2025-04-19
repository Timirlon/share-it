package org.example.shareitserver.items.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.items.comments.dtos.CommentReadDto;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemReadDto {
    Integer id;

    String name;

    String description;

    Boolean available;

    List<CommentReadDto> comments = new ArrayList<>();

    String owner;

    Integer requestId;
}
