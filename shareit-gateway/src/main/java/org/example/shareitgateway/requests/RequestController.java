package org.example.shareitgateway.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.shareitserver.requests.dtos.RequestCreateDto;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import static org.example.shareitserver.utils.RequestConstants.USER_ID_REQUEST_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestBody @Valid RequestCreateDto requestDto,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return requestClient.create(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllOfMine(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return requestClient.findAllOfMine(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllOfOthers(
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId,
            @RequestParam(defaultValue = "0") @Min(value = 0) int from,
            @RequestParam(defaultValue = "10") @Range(min = 1, max = 20) int size) {

        return requestClient.findAllOfOthers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable int requestId,
                                           @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        return requestClient.findById(requestId, userId);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleHttpRequest(HttpClientErrorException ex) {
        return new ResponseEntity<>(
                ex.getResponseBodyAsString(),
//                ex.getResponseHeaders(),
                ex.getStatusCode());
    }
}
