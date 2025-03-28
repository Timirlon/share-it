package org.example.shareit.requests;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareit.exceptions.NotFoundException;
import org.example.shareit.users.User;
import org.example.shareit.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@Service
public class RequestService {
    RequestRepository requestRepository;
    UserRepository userRepository;


    public Request create(Request request, int requestorId) {
        User requestor = getUserByIdOrElseThrow(requestorId);
        request.setRequestor(requestor);

        requestRepository.save(request);

        return request;
    }

    public List<Request> findAllByRequestorId(int requestorId) {
        getUserByIdOrElseThrow(requestorId);

        return requestRepository.findAllByRequestor_IdOrderByCreatedDesc(requestorId);
    }

    public Page<Request> findAllByRequestorIdExcludingOrderByCreation(int requestorId, int from, int size) {
        getUserByIdOrElseThrow(requestorId);

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        return requestRepository.findAllByRequestor_IdNotOrderByCreatedDesc(
                requestorId, pageable);
    }

    public Request findById(int requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден."));
    }

    private User getUserByIdOrElseThrow(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }
}
