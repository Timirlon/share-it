package org.example.shareit.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareit.requests.dtos.RequestCreateDto;
import org.example.shareit.requests.dtos.RequestMapper;
import org.example.shareit.requests.dtos.RequestReadDto;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.shareit.utils.RequestConstants.USER_ID_REQUEST_HEADER;

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
            @RequestBody @Valid RequestCreateDto requestDto,
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
            @RequestParam(defaultValue = "0") @Min(value = 0) int from,
            @RequestParam(defaultValue = "10") @Range(min = 1, max = 20) int size) {

        return mapper.toDto(
                requestService.findAllByRequesterIdExcludingOrderByCreation(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public RequestReadDto findById(@PathVariable int requestId) {
        return mapper.toDto(
                requestService.findById(requestId));
    }
}
