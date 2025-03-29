package org.example.shareit.users;

import static org.junit.jupiter.api.Assertions.*;

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
    void findByIdTestFail() {
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
}
