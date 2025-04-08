package org.example.shareit.users.dtos;

import org.example.shareit.users.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public UserReadDto toDto(User user) {
        UserReadDto dto = new UserReadDto();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        return dto;
    }

    public User fromDto(UserCreateDto dto) {
        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return user;
    }

    public User fromDto(UserUpdDto dto) {
        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return user;
    }

    public List<UserReadDto> toDto(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .toList();
    }
}
