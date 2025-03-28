package org.example.shareit.requests.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestCreateDto {
    @NotBlank(message = "Текст запроса не может быть пустым!")
    String description;
}
