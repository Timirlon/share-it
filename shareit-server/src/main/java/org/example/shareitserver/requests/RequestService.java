package org.example.shareitserver.requests;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.exceptions.NotFoundException;
import org.example.shareitserver.users.User;
import org.example.shareitserver.users.UserRepository;
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


    public Request create(Request request, int requesterId) {
        User requester = findUserByIdOrElseThrowNotFound(requesterId);
        request.setRequester(requester);

        requestRepository.save(request);

        return request;
    }

    public List<Request> findAllByRequesterId(int requesterId) {
        findUserByIdOrElseThrowNotFound(requesterId);

        return requestRepository.findAllByRequesterId_OrderByCreatedDesc(requesterId);
    }

    public Page<Request> findAllByRequesterIdNot_OrderByCreated(int requesterId, int from, int size) {
        findUserByIdOrElseThrowNotFound(requesterId);

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        return requestRepository.findAllByRequesterId_NotOrderByCreatedDesc(
                requesterId, pageable);
    }

    public Request findById(int requestId, int userId) {
        findUserByIdOrElseThrowNotFound(userId);

        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден."));
    }

    private User findUserByIdOrElseThrowNotFound(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }
}
