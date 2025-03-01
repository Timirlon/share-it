package org.example.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    int id;

    @NotBlank(message = "Название товара не может быть пустым!")
    String name;

    @NotBlank(message = "Описание товара не может быть пустым!")
    String description;

    @NotNull(message = "Требуется задать статус!")
    Boolean available;

    List<CommentReadDto> comments = new ArrayList<>();

    String owner;
}
