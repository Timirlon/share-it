package org.example.shareitserver.users;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.users.dtos.UserCreateDto;
import org.example.shareitserver.users.dtos.UserMapper;
import org.example.shareitserver.users.dtos.UserReadDto;
import org.example.shareitserver.users.dtos.UserUpdDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;
    UserMapper mapper;

    @GetMapping
    public List<UserReadDto> findAll() {
        return mapper.toDto(
                userService.findAll());
    }

    @GetMapping("/{id}")
    public UserReadDto findById(@PathVariable int id) {
        return mapper.toDto(
                userService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserReadDto create(@RequestBody UserCreateDto userDto) {
        User user = mapper.fromDto(userDto);

        return mapper.toDto(
                userService.create(user));
    }

    @PatchMapping("/{id}")
    public UserReadDto update(
            @PathVariable int id,
            @RequestBody UserUpdDto userDto) {
        User user = mapper.fromDto(userDto);

        return mapper.toDto(
                userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public UserReadDto deleteById(@PathVariable int id) {
        return mapper.toDto(
                userService.deleteById(id));
    }
}
