package org.example.shareitserver.items.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCreateDto {
    @NotBlank(message = "Название товара не может быть пустым!")
    String name;

    @NotBlank(message = "Описание товара не может быть пустым!")
    String description;

    @NotNull(message = "Требуется задать статус!")
    Boolean available;

    int requestId;
}
