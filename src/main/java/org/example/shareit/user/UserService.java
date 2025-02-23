package org.example.shareit.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.example.shareit.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserInMemoryDao dao;
    private final UserMapper mapper;

    public List<UserDto> findAll() {
        return dao.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public UserDto findById(int id) {
        return mapper.toDto(dao.findById(id));
    }

    public UserDto create(UserDto userDto) {
        User user = mapper.fromDto(userDto);
        validateEmail(user.getEmail());

        return mapper.toDto(dao.insert(user));
    }

    public UserDto update(int userId, UserDto userDto) {
        User user = mapper.fromDto(userDto);
        user.setId(userId);


        User oldUser = checkIfUserExistsById(userId);
        if (user.getName() == null) user.setName(oldUser.getName());
        if (user.getEmail() == null) user.setEmail(oldUser.getEmail());

        validateEmail(user.getEmail());

        return mapper.toDto(dao.insert(user));
    }

    public UserDto remove(int userId) {
        checkIfUserExistsById(userId);

        return mapper.toDto(dao.remove(userId));
    }

    private User checkIfUserExistsById(int userId) {
        User user = dao.findById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }

        return user;
    }

    private void validateEmail(String email) {
        if (email == null || email.isEmpty() || !email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            throw new ValidationException("Указан некорректный адрес эл.почты.");
        }
    }
}
