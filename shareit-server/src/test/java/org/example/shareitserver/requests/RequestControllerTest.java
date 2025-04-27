package org.example.shareitserver.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.shareitserver.items.Item;
import org.example.shareitserver.items.comments.dtos.CommentMapper;
import org.example.shareitserver.items.dtos.ItemMapper;
import org.example.shareitserver.requests.dtos.RequestCreateDto;
import org.example.shareitserver.requests.dtos.RequestMapper;
import org.example.shareitserver.users.User;
import org.example.shareitserver.users.dtos.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.example.shareitserver.utils.RequestConstants.USER_ID_REQUEST_HEADER;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest({RequestController.class, RequestMapper.class, ItemMapper.class, CommentMapper.class, UserMapper.class})
public class RequestControllerTest {
    static final String BASE_URL = "/requests";

    @MockitoBean
    RequestService requestService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @SneakyThrows
    void createTestSuccess() {
        int requestId = 1;
        String requestDesc = "create-test-desc-1";
        LocalDateTime now = LocalDateTime.now();

        RequestCreateDto requestDto = new RequestCreateDto();
        requestDto.setDescription(requestDesc);

        int requesterId = 1;


        Mockito.when(requestService.create(Mockito.any(), Mockito.anyInt()))
                .thenAnswer(
                        invocationOnMock -> {
                            Request returnRequest = new Request();
                            returnRequest.setId(requestId);
                            returnRequest.setDescription(requestDesc);
                            returnRequest.setCreated(now);

                            return returnRequest;
                        }
                );


        String requestAsJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestAsJson)
                    .header(USER_ID_REQUEST_HEADER, requesterId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value(requestDesc))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    @SneakyThrows
    void createWithBlankDescTestFail() {
        RequestCreateDto requestDto = new RequestCreateDto();

        String requestAsJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestAsJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void findAllOfMineTest() {
        int requesterId = 1;
        int firstRequestId = 1;
        String firstRequestDesc = "get-test-desc-1";
        int secondRequestId = 2;
        String secondRequestDesc = "get-test-desc-2";

        Request firstRequest = new Request();
        firstRequest.setId(firstRequestId);
        firstRequest.setDescription(firstRequestDesc);
        firstRequest.setCreated(LocalDateTime.now());

        Request secondRequest = new Request();
        secondRequest.setId(secondRequestId);
        secondRequest.setDescription(secondRequestDesc);
        secondRequest.setCreated(LocalDateTime.now());


        Mockito.when(requestService.findAllByRequesterId(requesterId))
                        .thenReturn(
                                List.of(firstRequest, secondRequest));


        mockMvc.perform(get(BASE_URL)
                        .header(USER_ID_REQUEST_HEADER, requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(firstRequestId))
                .andExpect(jsonPath("$.[0].description").value(firstRequestDesc))
                .andExpect(jsonPath("$.[0].created").exists())
                .andExpect(jsonPath("$.[1].id").value(secondRequestId))
                .andExpect(jsonPath("$.[1].description").value(secondRequestDesc))
                .andExpect(jsonPath("$.[1].created").exists());
    }

    @Test
    @SneakyThrows
    void findAllOfOthers() {
        int requesterId = 1;
        int firstRequestId = 1;
        String firstRequestDesc = "get-test-desc-1";
        int secondRequestId = 2;
        String secondRequestDesc = "get-test-desc-2";

        Request firstRequest = new Request();
        firstRequest.setId(firstRequestId);
        firstRequest.setDescription(firstRequestDesc);
        firstRequest.setCreated(LocalDateTime.now());

        Request secondRequest = new Request();
        secondRequest.setId(secondRequestId);
        secondRequest.setDescription(secondRequestDesc);
        secondRequest.setCreated(LocalDateTime.now());


        Mockito.when(requestService.findAllByRequesterIdNot_OrderByCreated(
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(new PageImpl<>(
                        List.of(firstRequest, secondRequest)));


        mockMvc.perform(get(BASE_URL + "/all")
                        .header(USER_ID_REQUEST_HEADER, requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(firstRequestId))
                .andExpect(jsonPath("$.[0].description").value(firstRequestDesc))
                .andExpect(jsonPath("$.[0].created").exists())
                .andExpect(jsonPath("$.[1].id").value(secondRequestId))
                .andExpect(jsonPath("$.[1].description").value(secondRequestDesc))
                .andExpect(jsonPath("$.[1].created").exists());
    }

    @Test
    @SneakyThrows
    void findById() {
        int userId = 1;

        int requestId = 1;
        String requestDesc = "get-test-desc-1";

        String requesterName = "get-test-user-name-1";
        User requester = new User();
        requester.setName(requesterName);

        String itemName = "get-test-item-name-1";
        String itemDesc = "get-test-item-desc-1";
        Item item = new Item();
        item.setId(1);
        item.setName(itemName);
        item.setDescription(itemDesc);
        item.setOwner(requester);
        item.setAvailable(true);

        Request request = new Request();
        request.setId(requestId);
        request.setDescription(requestDesc);
        request.setCreated(LocalDateTime.now());
        request.setResponseItems(List.of(item));


        Mockito.when(requestService.findById(requestId, userId))
                .thenReturn(request);


        mockMvc.perform(get(BASE_URL + "/" + requestId)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value(requestDesc))
                .andExpect(jsonPath("$.created").exists());
    }
}
