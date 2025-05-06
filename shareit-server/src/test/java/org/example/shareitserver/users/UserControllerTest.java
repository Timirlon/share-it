package org.example.shareitserver.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.shareitserver.exceptions.NotFoundException;
import org.example.shareitserver.users.dtos.UserCreateDto;
import org.example.shareitserver.users.dtos.UserMapper;
import org.example.shareitserver.users.dtos.UserReadDto;
import org.example.shareitserver.users.dtos.UserUpdDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, UserMapper.class})
public class UserControllerTest {
    @MockitoBean
    UserService userService;

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


        Mockito.when(userService.findAll())
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


        Mockito.when(userService.findById(userId))
                .thenReturn(user);


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

        Mockito.when(userService.findById(wrongId))
                .thenThrow(new NotFoundException("Пользователь не найден."));


        mockMvc.perform(get("/users/" + wrongId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedMessage))
                .andExpect(jsonPath("$.desc").value(expectedDesc));
    }

    @Test
    @SneakyThrows
    void createTestSuccess() {
        String userName = "test-create";
        String userEmail = "test-create@post.com";

        UserReadDto userDto = new UserReadDto();
        userDto.setName(userName);
        userDto.setEmail(userEmail);

        int userId = 1;


        Mockito.when(userService.create(Mockito.any(User.class)))
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

//    @Test
//    @SneakyThrows
//    void createWithBlankNameTest() {
//        String userName = "   ";
//        String userEmail = "test-create@post.com";
//
//        UserCreateDto userDto = new UserCreateDto();
//        userDto.setName(userName);
//        userDto.setEmail(userEmail);
//
//        Mockito.when(userService.create(Mockito.any(User.class)))
//                .thenAnswer(
//                        invocationOnMock -> {
//                            User returnUser = invocationOnMock.getArgument(0, User.class);
//                            returnUser.setId(1);
//                            returnUser.setName(userName);
//                            returnUser.setEmail(userEmail);
//
//                            return returnUser;
//                        });
//
//
//        String userDtoInJson = objectMapper.writeValueAsString(userDto);
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(userDtoInJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @SneakyThrows
//    void createWithEmailOfNullTest() {
//        String userName = "test-create";
//
//        UserCreateDto userDto = new UserCreateDto();
//        userDto.setName(userName);
//        userDto.setEmail(null);
//
//        Mockito.when(userService.create(Mockito.any(User.class)))
//                .thenAnswer(
//                        invocationOnMock -> {
//                            User returnUser = invocationOnMock.getArgument(0, User.class);
//                            returnUser.setId(1);
//                            returnUser.setName(userName);
//                            returnUser.setEmail(null);
//
//                            return returnUser;
//                        });
//
//
//        String userDtoInJson = objectMapper.writeValueAsString(userDto);
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(userDtoInJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @SneakyThrows
//    void createWithInvalidEmailTest() {
//        String userName = "test-create";
//        String userEmail = "//test@create@invalid.email";
//
//        UserCreateDto userDto = new UserCreateDto();
//        userDto.setName(userName);
//        userDto.setEmail(userEmail);
//
//        Mockito.when(userService.create(Mockito.any(User.class)))
//                .thenAnswer(
//                        invocationOnMock -> {
//                            User returnUser = invocationOnMock.getArgument(0, User.class);
//                            returnUser.setId(1);
//                            returnUser.setName(userName);
//                            returnUser.setEmail(userEmail);
//
//                            return returnUser;
//                        });
//
//
//        String userDtoInJson = objectMapper.writeValueAsString(userDto);
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(userDtoInJson))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    @SneakyThrows
    void updateTest() {
        int userId = 1;
        String userName = "test-update-1";
        String userEmail = "test-update-1@mail.com";

        UserUpdDto userUpd = new UserUpdDto();
        userUpd.setName(userName);


        Mockito.when(userService.update(Mockito.anyInt(), Mockito.any(User.class)))
                .thenAnswer(
                        invocationOnMock -> {
                            User returnUser = new User();
                            returnUser.setId(userId);
                            returnUser.setName(userName);
                            returnUser.setEmail(userEmail);

                            return returnUser;
                        }
                );


        String userInJson = objectMapper.writeValueAsString(userUpd);

        mockMvc.perform(patch("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userInJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail));
    }

    @Test
    @SneakyThrows
    void deleteTest() {
        int userId = 1;
        String userName = "test-delete-1";
        String userEmail = "test-delete-1@mail.com";

        User deleteUser = new User();
        deleteUser.setId(userId);
        deleteUser.setName(userName);
        deleteUser.setEmail(userEmail);


        Mockito.when(userService.deleteById(userId))
                .thenReturn(deleteUser);


        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail));
    }
}
