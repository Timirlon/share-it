package org.example.shareit.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.example.shareit.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public List<UserDto> findAll() {
        return mapper.toDto(userRepository.findAll());
    }

    public UserDto findById(int id) {
        return mapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден.")));
    }

    public UserDto create(UserDto userDto) {
        User user = mapper.fromDto(userDto);

        return mapper.toDto(userRepository.save(user));
    }

    public UserDto update(int userId, UserDto userDto) {
        User user = mapper.fromDto(userDto);
        user.setId(userId);


        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        if (user.getName() == null) user.setName(oldUser.getName());
        if (user.getEmail() == null) user.setEmail(oldUser.getEmail());

        validateEmail(user.getEmail());

        return mapper.toDto(userRepository.save(user));
    }

    public UserDto deleteById(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        userRepository.deleteById(userId);

        return mapper.toDto(user);
    }

    private void validateEmail(String email) {
        if (email == null || email.isEmpty() || !email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            throw new ValidationException("Некорректный адрес эл.почты.");
        }
    }
}
