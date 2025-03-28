package org.example.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Integer id;

    @NotBlank(message = "Имя пользователя не может быть пустым!")
    String name;

    @Email(message = "Некорректный адрес эл.почты")
    String email;
}
