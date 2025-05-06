package org.example.shareitserver.requests;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.requests.dtos.RequestCreateDto;
import org.example.shareitserver.requests.dtos.RequestMapper;
import org.example.shareitserver.requests.dtos.RequestReadDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.shareitserver.utils.RequestConstants.USER_ID_REQUEST_HEADER;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@RestController
@RequestMapping("/requests")
public class RequestController {
    RequestService requestService;
    RequestMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestReadDto create(
            @RequestBody RequestCreateDto requestDto,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        Request request = mapper.fromDto(requestDto);

        return mapper.toDto(
                requestService.create(request, userId));
    }

    @GetMapping
    public List<RequestReadDto> findAllOfMine(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        return mapper.toDto(
                requestService.findAllByRequesterId(userId));
    }

    @GetMapping("/all")
    public List<RequestReadDto> findAllOfOthers(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return mapper.toDto(
                requestService.findAllByRequesterIdNot_OrderByCreated(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public RequestReadDto findById(@PathVariable int requestId,
                                   @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        return mapper.toDto(
                requestService.findById(requestId, userId));
    }
}
