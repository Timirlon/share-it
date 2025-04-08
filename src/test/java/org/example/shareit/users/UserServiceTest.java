package org.example.shareit.users;

import static org.junit.jupiter.api.Assertions.*;

import lombok.SneakyThrows;
import org.example.shareit.exceptions.DataConflictException;
import org.example.shareit.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    void findAllTest() {
        User userOne = new User();
        userOne.setId(1);
        userOne.setName("test-1");
        userOne.setEmail("test-1@mail.com");

        User userTwo = new User();
        userTwo.setId(2);
        userTwo.setName("test-2");
        userTwo.setEmail("test-2@mail.com");

        int expectedSize = 2;


        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(userOne, userTwo));

        List<User> userList = userService.findAll();


        assertEquals(expectedSize, userList.size());
        assertEquals(userOne.getId(), userList.get(0).getId());
        assertEquals(userTwo.getId(), userList.get(1).getId());
    }

    @Test
    void findByIdTestSuccess() {
        User user = new User();
        user.setId(1);
        user.setName("test-1");
        user.setEmail("test-1@mail.com");


        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));


        User foundUser = userService.findById(user.getId());

        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void findByIdTestUserNotFoundFail() {
        String expectedMessage = "Пользователь не найден.";
        int wrongId = 999;

        Mockito.when(userRepository.findById(wrongId))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.findById(wrongId));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void createTestSuccess() {
        User requestUser = new User();
        requestUser.setName("test-1");
        requestUser.setEmail("test-1@email.com");

        User savedUser = userService.create(requestUser);


        assertEquals(requestUser.getName(), savedUser.getName());
        assertEquals(requestUser.getEmail(), savedUser.getEmail());
    }

    @Test
    void createWithAlreadyTakenEmailTest() {
        int firstUserId = 1;
        String firstUserName = "create-test-1";
        String firstUserEmail = "create-test-1@mail.com";

        User user = new User();
        user.setId(firstUserId);
        user.setName(firstUserName);
        user.setEmail(firstUserEmail);


        Mockito.when(userRepository.findByEmail(firstUserEmail))
                .thenReturn(Optional.of(user));


        assertThrows(DataConflictException.class, () -> userService.create(user));
    }

    @Test
    @SneakyThrows
    void updateUserNameTestSuccess() {
        int userId = 1;
        String initialUserName = "update-test-1";
        String initialUserEmail = "update-test-1@mail.com";

        User initialUser =  new User();
        initialUser.setId(userId);
        initialUser.setName(initialUserName);
        initialUser.setEmail(initialUserEmail);


        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(initialUser));

        String updUserName = "update-test-2";
        User updUser = new User();
        updUser.setName(updUserName);

        User returnedValue = userService.update(userId, updUser);


        assertEquals(userId, returnedValue.getId());
        assertEquals(updUserName, returnedValue.getName());
        assertEquals(initialUserEmail, returnedValue.getEmail());
    }

    @Test
    @SneakyThrows
    void updateUserEmailTestSuccess() {
        int userId = 1;
        String initialUserName = "update-test-1";
        String initialUserEmail = "update-test-1@mail.com";

        User initialUser =  new User();
        initialUser.setId(userId);
        initialUser.setName(initialUserName);
        initialUser.setEmail(initialUserEmail);


        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(initialUser));

        String updUserEmail = "update-test-2@mail.com";
        User updUser = new User();
        updUser.setEmail(updUserEmail);

        User returnedValue = userService.update(userId, updUser);


        assertEquals(userId, returnedValue.getId());
        assertEquals(initialUserName, returnedValue.getName());
        assertEquals(updUserEmail, returnedValue.getEmail());
    }

    @Test
    @SneakyThrows
    void updateUserEmailTestEmailIsTakenFail() {
        String expectedMessage = "Данная эл.почта занята.";

        int userId = 1;
        String initialUserName = "update-test-1";
        String initialUserEmail = "update-test-1@mail.com";

        User initialUser =  new User();
        initialUser.setId(userId);
        initialUser.setName(initialUserName);
        initialUser.setEmail(initialUserEmail);

        String updUserEmail = "update-test-2@mail.com";
        User updUser = new User();
        updUser.setEmail(updUserEmail);


        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(initialUser));

        Mockito.when(userRepository.findByEmail(updUserEmail))
                .thenReturn(Optional.of(updUser));


        DataConflictException ex = assertThrows(DataConflictException.class,
                () -> userService.update(userId, updUser));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    @SneakyThrows
    void updateTestUserNotFoundFail() {
        String expectedMessage = "Пользователь не найден.";

        int userId = 1;
        String updatedUserName = "update-test-1";
        String updatedUserEmail = "update-test@mail.com";
        User userUpd = new User();
        userUpd.setName(updatedUserName);
        userUpd.setEmail(updatedUserEmail);


        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());


        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.update(userId, userUpd));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    @SneakyThrows
    void deleteByIdTestSuccess() {
        int userId = 1;
        String expectedUserName = "delete-test-1";
        String expectedUserEmail = "delete-test-1@mail.com";

        User userDelete = new User();
        userDelete.setId(userId);
        userDelete.setName(expectedUserName);
        userDelete.setEmail(expectedUserEmail);


        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(userDelete));

        User deletedUser = userService.deleteById(userId);


        assertEquals(userId, deletedUser.getId());
        assertEquals(expectedUserName, deletedUser.getName());
        assertEquals(expectedUserEmail, deletedUser.getEmail());
    }

    @Test
    @SneakyThrows
    void deleteByIdTestUserNotFoundFail() {
        String expectedMessage = "Пользователь не найден.";
        int userId = 1;


        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());


        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.deleteById(userId));

        assertEquals(expectedMessage, ex.getMessage());
    }
}
