package org.example.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.example.shareit.exception.NotFoundException;
import org.example.shareit.user.User;
import org.example.shareit.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    private final RequestMapper requestMapper;


    public RequestReadDto create(RequestCreateDto dto, int requestorId) {
        Request request = requestMapper.fromDto(dto);

        User requestor = getUserById(requestorId);
        request.setRequestor(requestor);


        Request readRequest = requestRepository.save(request);
        return requestMapper.toDto(readRequest);
    }

    public List<RequestReadDto> findAllByRequestorId(int requestorId) {
        getUserById(requestorId);

        return requestMapper.toDto(
                requestRepository.findAllByRequestor_IdOrderByCreatedDesc(requestorId));
    }

    public List<RequestReadDto> findAllByRequestorIdExcludingOrderByCreation(int requestorId, int from, int size) {
        getUserById(requestorId);


        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        return requestMapper.toDto(
                requestRepository.findAllByRequestor_IdNotOrderByCreatedDesc(requestorId, pageable));
    }

    public RequestReadDto findById(int requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден."));

        return requestMapper.toDto(request);
    }

    private User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }
}
