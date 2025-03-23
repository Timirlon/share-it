package org.example.shareit.user;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Spy
    UserMapper userMapper;

    @Test
    void findByIdTest() {
        User user = new User();
        user.setId(1);
        user.setName("Вася");
        user.setEmail("VasilyTyomny@mail.com");


        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));


        UserDto foundUser = userService.findById(user.getId());

        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }
}
