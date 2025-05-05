package org.example.shareitgateway.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.shareitserver.users.dtos.UserCreateDto;
import org.example.shareitserver.users.dtos.UserUpdDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable int id) {
        return userClient.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody UserCreateDto userDto) {
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable int id,
            @Valid @RequestBody UserUpdDto userDto) {
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable int id) {
        return userClient.deleteById(id);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleHttpRequest(HttpClientErrorException ex) {
        return new ResponseEntity<>(
                ex.getResponseBodyAsString(),
//                ex.getResponseHeaders(),
                ex.getStatusCode());
    }
}
