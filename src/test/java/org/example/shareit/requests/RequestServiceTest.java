package org.example.shareit.requests;

import lombok.SneakyThrows;
import org.example.shareit.exceptions.NotFoundException;
import org.example.shareit.users.User;
import org.example.shareit.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    RequestRepository requestRepository;

    @InjectMocks
    RequestService requestService;

    @Test
    @SneakyThrows
    void createTestSuccess() {
        String requestDesc = "create-test-desc-1";
        Request request = new Request();
        request.setDescription(requestDesc);

        int requesterId = 1;
        String userName = "create-test-user-name";
        String userEmail = "create-test-user-email@mail.com";
        User requester = new User();
        requester.setId(requesterId);
        requester.setName(userName);
        requester.setEmail(userEmail);

        Mockito.when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(requester));


        Request createdRequest = requestService.create(request, requesterId);


        assertFalse(createdRequest.getId() != 0);
        assertEquals(requestDesc, createdRequest.getDescription());
        assertEquals(requesterId, createdRequest.getRequester().getId());
    }

    @Test
    @SneakyThrows
    void createTestRequesterNotFoundFail() {
        String expectedMessage = "Пользователь не найден.";

        String requestDesc = "create-test-desc-1";
        Request request = new Request();
        request.setDescription(requestDesc);

        int requesterId = 1;


        Mockito.when(userRepository.findById(requesterId))
                .thenReturn(Optional.empty());


        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.create(request, requesterId));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    @SneakyThrows
    void findAllByRequesterIdTestSuccess() {
        int expectedSize = 2;

        int requesterId = 1;
        String requesterName = "create-test-user-name-1";
        String requesterEmail = "create-test-user-email@mail.com";
        User requester = new User();
        requester.setId(requesterId);
        requester.setName(requesterName);
        requester.setEmail(requesterEmail);

        int firstRequestId = 1;
        String firstRequestDesc = "create-test-desc-1";
        Request firstRequest = new Request();
        firstRequest.setId(firstRequestId);
        firstRequest.setDescription(firstRequestDesc);
        firstRequest.setCreated(LocalDateTime.now());
        firstRequest.setRequester(requester);

        int secondRequestId = 2;
        String secondRequestDesc = "create-test-desc-2";
        Request secondRequest = new Request();
        secondRequest.setId(secondRequestId);
        secondRequest.setDescription(secondRequestDesc);
        secondRequest.setCreated(LocalDateTime.now());
        secondRequest.setRequester(requester);


        Mockito.when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(requester));


        Mockito.when(requestRepository.findAllByRequester_IdOrderByCreatedDesc(requesterId))
                .thenReturn(List.of(firstRequest, secondRequest));


        List<Request> requests = requestService.findAllByRequesterId(requesterId);

        assertEquals(expectedSize, requests.size());
        assertEquals(firstRequestId, requests.get(0).getId());
        assertEquals(firstRequestDesc, requests.get(0).getDescription());
        assertEquals(requesterId, requests.get(0).getRequester().getId());
        assertEquals(secondRequestId, requests.get(1).getId());
        assertEquals(secondRequestDesc, requests.get(1).getDescription());
        assertEquals(requesterId, requests.get(1).getRequester().getId());
    }

    @Test
    @SneakyThrows
    void findAllByRequesterIdTestRequesterNotFoundFail() {
        String expectedMessage = "Пользователь не найден.";
        int requesterId = 1;

        Mockito.when(userRepository.findById(requesterId))
                .thenReturn(Optional.empty());


        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.findAllByRequesterId(requesterId));
        assertEquals(expectedMessage, ex.getMessage());
    }


    @Test
    @SneakyThrows
    void findAllByRequesterIdExcludingTestSuccess() {
        int expectedSize = 2;

        int requesterId = 1;
        User requester = new User();
        requester.setId(requesterId);

        int firstRequestId = 1;
        String firstRequestDesc = "create-test-desc-1";
        Request firstRequest = new Request();
        firstRequest.setId(firstRequestId);
        firstRequest.setDescription(firstRequestDesc);
        firstRequest.setCreated(LocalDateTime.now());
        firstRequest.setRequester(requester);

        int secondRequestId = 2;
        String secondRequestDesc = "create-test-desc-2";
        Request secondRequest = new Request();
        secondRequest.setId(secondRequestId);
        secondRequest.setDescription(secondRequestDesc);
        secondRequest.setCreated(LocalDateTime.now());
        secondRequest.setRequester(requester);


        Mockito.when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(requester));

        Mockito.when(requestRepository.findAllByRequester_IdNotOrderByCreatedDesc(
                Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(
                        List.of(firstRequest, secondRequest)));


        Page<Request> requestPage = requestService.findAllByRequesterIdExcludingOrderByCreation(
                requesterId, 0, 2);

        assertEquals(expectedSize, requestPage.getTotalElements());
    }

    @Test
    @SneakyThrows
    void findAllByRequesterIdExcludingTestSuccess_UserNotFoundFail() {
        String expectedMessage = "Пользователь не найден.";
        int requesterId = 1;

        Mockito.when(userRepository.findById(requesterId))
                .thenReturn(Optional.empty());


        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.findAllByRequesterIdExcludingOrderByCreation(
                        requesterId, 0, 2));

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    @SneakyThrows
    void findByIdTestSuccess() {
        int requesterId = 1;
        User requester = new User();
        requester.setId(requesterId);

        int requestId = 1;
        String requestDesc = "get-test-request-desc-1";
        LocalDateTime now = LocalDateTime.now();

        Request request = new Request();
        request.setId(requestId);
        request.setDescription(requestDesc);
        request.setRequester(requester);
        request.setCreated(now);


        Mockito.when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(request));


        Request foundRequest = requestService.findById(requestId);

        assertEquals(requestId, foundRequest.getId());
        assertEquals(requestDesc, foundRequest.getDescription());
        assertEquals(requesterId, foundRequest.getRequester().getId());
        assertEquals(now, foundRequest.getCreated());
    }

    @Test
    @SneakyThrows
    void findByIdTestRequestNotFoundFail() {
        String expectedMessage = "Запрос не найден.";
        int requestId = 1;


        Mockito.when(requestRepository.findById(requestId))
                .thenReturn(Optional.empty());


        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.findById(requestId));

        assertEquals(expectedMessage, ex.getMessage());
    }
}
