package org.example.shareit.user;

import lombok.RequiredArgsConstructor;
import org.example.shareit.exception.DataConflictException;
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

        validateEmailIsNotTaken(user.getEmail());

        return mapper.toDto(userRepository.save(user));
    }

    public UserDto update(int userId, UserUpdDto dto) {
        User user = mapper.fromDto(dto);
        user.setId(userId);


        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        } else {
            validateEmailIsNotTaken(user.getEmail());
        }


        return mapper.toDto(userRepository.save(user));
    }

    public UserDto deleteById(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        userRepository.deleteById(userId);

        return mapper.toDto(user);
    }

    private void validateEmailIsNotTaken(String email) {
        if (userRepository.findByEmail(email) != null) {
            throw new DataConflictException("Данная эл.почта занята.");
        }
    }
}
