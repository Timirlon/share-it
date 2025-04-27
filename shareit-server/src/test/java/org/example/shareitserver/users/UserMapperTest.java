package org.example.shareitserver.users;

import org.example.shareitserver.users.dtos.UserCreateDto;
import org.example.shareitserver.users.dtos.UserMapper;
import org.example.shareitserver.users.dtos.UserReadDto;
import static org.junit.jupiter.api.Assertions.*;

import org.example.shareitserver.users.dtos.UserUpdDto;
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
    void fromCreateDtoTest() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("test-name");
        dto.setEmail("test-email");


        User user = userMapper.fromDto(dto);


        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }

    @Test
    void fromUpdateDtoTest() {
        UserUpdDto dto = new UserUpdDto();
        dto.setName("test-name");
        dto.setEmail("test-email");


        User user = userMapper.fromDto(dto);


        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }
}
