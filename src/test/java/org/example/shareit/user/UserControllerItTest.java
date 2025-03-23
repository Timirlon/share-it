package org.example.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerItTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void findAllTest() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Temirlan"))
                .andExpect(jsonPath("$[0].email").value("tturgimbayev@gmail.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Вася"))
                .andExpect(jsonPath("$[1].email").value("v.pupking@email.org"));
    }

    @Test
    @SneakyThrows
    void findByIdTest() {
        int userId = 2;

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Вася"))
                .andExpect(jsonPath("$.email").value("v.pupking@email.org"));
    }

    @Test
    @SneakyThrows
    void createTest() {
        UserDto userDto = new UserDto();

        String userName = "Eminem";
        String userEmail = "guess.who@is.back";
        userDto.setName(userName);
        userDto.setEmail(userEmail);

        String json = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail));
    }

    @Test
    @SneakyThrows
    void updateTest() {
        int userId = 2;

        //Проверяем что объект существует
        mockMvc.perform(
                get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());


        UserDto userDto = new UserDto();

        String updUserName = "Vasiliy";
        userDto.setName(updUserName);


        String json = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                patch("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(updUserName))
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    @SneakyThrows
    void deleteTest() {
        int userId = 2;

        //объект существует
        mockMvc.perform(
                get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        //Удаление
        mockMvc.perform(
                delete("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));

        //не сущ.
        mockMvc.perform(
                get("/users/" + userId))
                .andExpect(status().isNotFound());
    }
}
