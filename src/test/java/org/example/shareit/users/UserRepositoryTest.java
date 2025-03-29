package org.example.shareit.users;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void saveTest() {
        User user = new User();

        String userName = "test-save";
        String userEmail = "test-save@mail.com";
        user.setName(userName);
        user.setEmail(userEmail);
        userRepository.save(user);


        User foundUser = userRepository.findById(user.getId()).orElseThrow();


        assertEquals(userName, foundUser.getName());
        assertEquals(userEmail, foundUser.getEmail());
    }

    @Test
    void findByEmailTest() {
        User user = new User();

        String userName = "test-save";
        String userEmail = "test-save@mail.com";
        user.setName(userName);
        user.setEmail(userEmail);
        userRepository.save(user);


        User foundUser = userRepository.findByEmail(userEmail).orElseThrow();

        assertEquals(userName, foundUser.getName());
        assertEquals(userEmail, foundUser.getEmail());
    }
}
