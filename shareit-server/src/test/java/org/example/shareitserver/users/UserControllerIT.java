package org.example.shareitserver.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.shareitserver.users.dtos.UserCreateDto;
import org.example.shareitserver.users.dtos.UserReadDto;
import org.example.shareitserver.users.dtos.UserUpdDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void updateTest() {
        //Создаем объект сперва
        String initialUserName = "test-create";
        String initialUserEmail = "test-create@post.com";

        UserCreateDto userDto = new UserCreateDto();
        userDto.setName(initialUserName);
        userDto.setEmail(initialUserEmail);

        String userDtoInJson = objectMapper.writeValueAsString(userDto);

        String userStr = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoInJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(initialUserName))
                .andExpect(jsonPath("$.email").value(initialUserEmail))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        UserReadDto mappedUserDto = objectMapper.readValue(userStr, UserReadDto.class);
        int userId = mappedUserDto.getId();

        //Проверяем что объект существует
        mockMvc.perform(
                get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(initialUserName))
                .andExpect(jsonPath("$.email").value(initialUserEmail));



        //Обновление
        UserUpdDto userUpdDto = new UserUpdDto();

        String updUserName = "test-update";
        userUpdDto.setName(updUserName);


        String json = objectMapper.writeValueAsString(userUpdDto);

        mockMvc.perform(
                patch("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(updUserName))
                .andExpect(jsonPath("$.email").value(initialUserEmail));
    }

    @Test
    @SneakyThrows
    void deleteTest() {
        //Создание объекта
        String userName = "test-create";
        String userEmail = "test-create@post.com";

        UserCreateDto userDto = new UserCreateDto();
        userDto.setName(userName);
        userDto.setEmail(userEmail);

        String userDtoInJson = objectMapper.writeValueAsString(userDto);

        String userStr = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoInJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        UserReadDto mappedUserDto = objectMapper.readValue(userStr, UserReadDto.class);
        int userId = mappedUserDto.getId();

        //Проверяем что объект существует
        mockMvc.perform(
                        get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail));


        //Удаление
        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail));


        //Проверяем что объекта больше нет
        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createWithAlreadyTakenEmailTest() {
        String errorTitle = "Конфликт данных.";
        String errorDesc = "Данная эл.почта занята.";


        String userOneName = "test-create-1";
        String userOneEmail = "test-create@post.com";

        UserCreateDto userOneDto = new UserCreateDto();
        userOneDto.setName(userOneName);
        userOneDto.setEmail(userOneEmail);


        String userOneInJson = objectMapper.writeValueAsString(userOneDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userOneInJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(userOneName))
                .andExpect(jsonPath("$.email").value(userOneEmail));


        String userTwoName = "test-create-2";
        UserCreateDto userTwoDto = new UserCreateDto();
        userTwoDto.setName(userTwoName);
        userTwoDto.setEmail(userOneEmail);

        String userTwoInJson = objectMapper.writeValueAsString(userTwoDto);

        mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userTwoInJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(errorTitle))
                .andExpect(jsonPath("$.desc").value(errorDesc));
    }
}
