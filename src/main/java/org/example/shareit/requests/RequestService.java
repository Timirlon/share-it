package org.example.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.example.shareit.exception.NotFoundException;
import org.example.shareit.user.User;
import org.example.shareit.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    RequestRepository requestRepository;
    UserRepository userRepository;

    RequestMapper requestMapper;


    public RequestReadDto create(RequestCreateDto dto, int requestorId) {
        Request request = requestMapper.fromDto(dto);

        User requestor = checkIfUserExistsById(requestorId);
        request.setRequestor(requestor);


        Request readRequest = requestRepository.save(request);
        return requestMapper.toDto(readRequest);
    }

    public List<RequestReadDto> findAllByRequestorId(int requestorId) {
        checkIfUserExistsById(requestorId);

        return requestMapper.toDto(
                requestRepository.findAllByRequestor_IdOrderByCreatedDesc(requestorId));
    }

    public List<RequestReadDto> findAllByRequestorIdExcludingOrderByCreation(int requestorId, int from, int size) {
        if (from < 0) {
            throw new IndexOutOfBoundsException();
        }

        checkIfUserExistsById(requestorId);

        return requestMapper.toDto(
                requestRepository.findAllByRequestor_IdNotOrderByCreatedDesc(requestorId));
    }

    public RequestReadDto findById(int requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден."));

        return requestMapper.toDto(request);
    }

    private User checkIfUserExistsById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }
}
