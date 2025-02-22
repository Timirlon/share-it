package org.example.shareit.user;

import lombok.RequiredArgsConstructor;
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

    public User create(UserDto userDto) {
        User user = mapper.fromDto(userDto);

        if (checkIfUserExists(user)) {
            return null;
        }

        return dao.insert(user);
    }

    public User update(UserDto userDto) {
        User user = mapper.fromDto(userDto);

        if (!checkIfUserExists(user)) {
            return null;
        }

        return dao.insert(user);
    }

    private boolean checkIfUserExists(User user) {
        return dao.findById(user.getId()) != null;
    }
}
