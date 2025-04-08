package org.example.shareit.users.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateDto {
    @NotBlank(message = "Имя пользователя не может быть пустым!")
    String name;

    @NotBlank
    @Email(message = "Некорректный адрес эл.почты")
    String email;
}
