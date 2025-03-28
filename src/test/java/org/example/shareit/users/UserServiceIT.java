package org.example.shareit.users;

import static org.junit.jupiter.api.Assertions.*;
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

        //Проверка по id
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
}
