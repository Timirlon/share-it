package org.example.shareitserver.requests;

import org.example.shareitserver.users.User;
import org.example.shareitserver.users.UserRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    RequestRepository requestRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void findAllByRequester_IdOrderByCreatedDescTest() {
        int expectedSize = 1;

        String requesterName = "get-test-user-name-1";
        String requesterEmail = "get-test-user-email-1";
        User requester = new User();
        requester.setName(requesterName);
        requester.setEmail(requesterEmail);
        userRepository.save(requester);


        String requestDesc = "get-test-request-desc-1";
        LocalDateTime now = LocalDateTime.now();
        Request request = new Request();
        request.setDescription(requestDesc);
        request.setCreated(now);
        request.setRequester(requester);


        requestRepository.save(request);

        List<Request> foundRequests = requestRepository.findAllByRequesterId_OrderByCreatedDesc(requester.getId());


        assertEquals(expectedSize, foundRequests.size());
        assertEquals(request.getId(), foundRequests.get(0).getId());
    }

    @Test
    void findAllByRequester_IdNotOrderByCreatedDescTest() {
        int page = 0;
        int size = 5;
        int expectedNumOfElements = 1;
        int wrongRequesterId = 999;

        String requesterName = "get-test-user-name-1";
        String requesterEmail = "get-test-user-email-1";
        User requester = new User();
        requester.setName(requesterName);
        requester.setEmail(requesterEmail);
        userRepository.save(requester);


        String requestDesc = "get-test-request-desc-1";
        LocalDateTime now = LocalDateTime.now();
        Request request = new Request();
        request.setDescription(requestDesc);
        request.setCreated(now);
        request.setRequester(requester);


        requestRepository.save(request);

        Page<Request> foundRequests =
                requestRepository.findAllByRequesterId_NotOrderByCreatedDesc(
                        wrongRequesterId, PageRequest.of(page, size));


        assertEquals(size, foundRequests.getSize());
        assertEquals(expectedNumOfElements, foundRequests.getTotalElements());
        assertEquals(request.getId(), foundRequests.toList().get(0).getId());
    }
}
