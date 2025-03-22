package org.example.shareit.requests;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.shareit.utils.RequestConstants.USER_ID_REQUEST_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {
    RequestService requestService;

    @PostMapping
    public RequestReadDto create(
            @RequestBody @Valid RequestCreateDto request,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        return requestService.create(request, userId);
    }

    @GetMapping
    public List<RequestReadDto> findAllOfMine(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        return requestService.findAllByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<RequestReadDto> findAllOfOthers(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam int from,
            @RequestParam int size) {

        return requestService.findAllByRequestorIdExcludingOrderByCreation(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestReadDto findById(@PathVariable int requestId) {
        return requestService.findById(requestId);
    }
}
