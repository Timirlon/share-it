package org.example.shareit.users;

import org.example.shareit.users.dtos.UserCreateDto;
import org.example.shareit.users.dtos.UserMapper;
import org.example.shareit.users.dtos.UserReadDto;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UserMapperTest {
    private final UserMapper userMapper = new UserMapper();

    @Test
    void toDtoTest() {
        User user = new User();
        user.setId(1);
        user.setName("test-name");
        user.setEmail("test-email");

        UserReadDto dto = userMapper.toDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void fromDtoTest() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("test-name");
        dto.setEmail("test-email");


        User user = userMapper.fromDto(dto);


        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }
}
