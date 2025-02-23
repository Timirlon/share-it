package org.example.shareit.user;

import org.example.shareit.exception.DataConflictException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserInMemoryDao {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> userEmails = new HashSet<>();
    private int idCounter = 0;

    public Collection<User> findAll() {
        return users.values();
    }

    public User findById(int id) {
        return users.get(id);
    }

    public User insert(User user) {
        User oldUser = users.get(user.getId());
        if (oldUser != null) {
            userEmails.remove(oldUser.getEmail());
        }

        if (userEmails.contains(user.getEmail())) {
            throw new DataConflictException("Данный адрес эл.почты занят.");
        }

        if (user.getId() == 0) {
            user.setId(++idCounter);
        }

        userEmails.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    public User remove(int id) {
        User removedUser = users.remove(id);
        userEmails.remove(removedUser.getEmail());

        return removedUser;
    }
}
