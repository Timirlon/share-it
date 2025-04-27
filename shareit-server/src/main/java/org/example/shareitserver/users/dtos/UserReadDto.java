package org.example.shareitserver.users.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserReadDto {
    Integer id;
    String name;
    String email;
}
