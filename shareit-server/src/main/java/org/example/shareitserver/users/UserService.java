package org.example.shareitserver.users;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.exceptions.DataConflictException;
import org.example.shareitserver.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@Service
public class UserService {
    UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    public User create(User user) {
//        validateEmailIsNotTaken(user.getEmail());

        userRepository.save(user);
        return user;
    }

    public User update(int userId, User user) {
        user.setId(userId);


        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }

        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());

        }

        if (!user.getEmail().equals(oldUser.getEmail())) {

            validateEmailIsNotTaken(user.getEmail());
        }

        userRepository.save(user);
        return user;
    }

    public User deleteById(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        userRepository.deleteById(userId);

        return user;
    }

    private void validateEmailIsNotTaken(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DataConflictException("Данная эл.почта занята.");
        }
    }
}
