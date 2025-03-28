package org.example.shareit.users;

import jakarta.persistence.EntityManager;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    void saveTest() {
        User user = new User();

        String userName = "Человек";
        String userEmail = "chel123@mail.com";
        user.setName(userName);
        user.setEmail(userEmail);
        userRepository.save(user);


        entityManager.clear();

        User foundUser = userRepository.findById(user.getId()).orElseThrow();


        assertEquals(userName, foundUser.getName());
        assertEquals(userEmail, foundUser.getEmail());
    }
}
