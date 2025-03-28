package org.example.shareit.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.shareit.exceptions.NotFoundException;
import org.example.shareit.users.dtos.UserMapper;
import org.example.shareit.users.dtos.UserReadDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, UserService.class, UserMapper.class})
public class UserControllerTest {
    @MockitoBean
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @SneakyThrows
    void findAllTest() {
        User userOne = new User();
        userOne.setId(1);
        userOne.setName("test-1");
        userOne.setEmail("test-1@mail.com");

        User userTwo = new User();
        userTwo.setId(2);
        userTwo.setName("test-2");
        userTwo.setEmail("test-2@mail.com");

        int expectedLength = 2;


        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(userOne, userTwo));


        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedLength))
                .andExpect(jsonPath("$[0].id").value(userOne.getId()))
                .andExpect(jsonPath("$[0].name").value(userOne.getName()))
                .andExpect(jsonPath("$[0].email").value(userOne.getEmail()))
                .andExpect(jsonPath("$[1].id").value(userTwo.getId()))
                .andExpect(jsonPath("$[1].name").value(userTwo.getName()))
                .andExpect(jsonPath("$[1].email").value(userTwo.getEmail()));
    }

    @Test
    @SneakyThrows
    void findByIdTestSuccess() {
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setName("test-1");
        user.setEmail("test-1@mail.com");


        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));


        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @SneakyThrows
    void findByIdTestFail() {
        String expectedMessage = "Объект не найден.";
        String expectedDesc = "Пользователь не найден.";
        int wrongId = 999;

        Mockito.when(userRepository.findById(wrongId))
                .thenReturn(Optional.empty());


        mockMvc.perform(get("/users/" + wrongId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedMessage))
                .andExpect(jsonPath("$.desc").value(expectedDesc));
    }

    @Test
    @SneakyThrows
    void createTest() {
        String userName = "test-create";
        String userEmail = "test-create@post.com";

        UserReadDto userDto = new UserReadDto();
        userDto.setName(userName);
        userDto.setEmail(userEmail);

        int userId = 1;


        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(
                        invocationOnMock -> {
                            User returnUser = invocationOnMock.getArgument(0, User.class);
                            returnUser.setId(userId);
                            returnUser.setName(userName);
                            returnUser.setEmail(userEmail);

                            return returnUser;
                        });


        String userDtoInJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userDtoInJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail));
    }
}
