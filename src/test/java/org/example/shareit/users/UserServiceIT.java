package org.example.shareit.users;

import static org.junit.jupiter.api.Assertions.*;

import org.example.shareit.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserServiceIT {
    @Autowired
    UserService userService;

    @Test
    void updateTest() {
        //Создание объекта
        String initialUserName = "test-create";
        String initialUserEmail = "test-create@post.com";

        User initialUser = new User();
        initialUser.setName(initialUserName);
        initialUser.setEmail(initialUserEmail);

        userService.create(initialUser);
        int userId = initialUser.getId();

        //Проверка, что объект существует
        User foundUser = userService.findById(userId);

        assertEquals(userId, foundUser.getId());
        assertEquals(initialUserName, foundUser.getName());
        assertEquals(initialUserEmail, foundUser.getEmail());


        //Обновление
        String updUserEmail = "test-upd@post.com";
        User updUser = new User();
        updUser.setEmail(updUserEmail);

        userService.update(userId, updUser);


        assertEquals(userId, updUser.getId());
        assertEquals(initialUserName, updUser.getName());
        assertEquals(updUserEmail, updUser.getEmail());
    }

    @Test
    void deleteTest() {
        //Создание объекта
        String userName = "test-create";
        String userEmail = "test-create@post.com";

        User initialUser = new User();
        initialUser.setName(userName);
        initialUser.setEmail(userEmail);

        userService.create(initialUser);
        int userId = initialUser.getId();


        User foundUser = userService.findById(userId);

        assertEquals(userId, foundUser.getId());
        assertEquals(userName, foundUser.getName());
        assertEquals(userEmail, foundUser.getEmail());


        User deletedUser = userService.deleteById(userId);

        assertEquals(userId, deletedUser.getId());
        assertEquals(userName, deletedUser.getName());
        assertEquals(userEmail, deletedUser.getEmail());


        assertThrows(NotFoundException.class, () -> userService.findById(userId));
    }
}
