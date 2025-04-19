package org.example.shareitgateway.items;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.shareitserver.items.comments.dtos.CommentCreateDto;
import org.example.shareitserver.items.dtos.ItemCreateDto;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import static org.example.shareitserver.utils.RequestConstants.USER_ID_REQUEST_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(USER_ID_REQUEST_HEADER) int userId,
                                  @RequestParam(defaultValue = "0") @Min(value = 0) int from,
                                  @RequestParam(defaultValue = "10") @Range(min = 1, max = 20) int size) {

        return itemClient.findAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable int id) {
        return itemClient.findById(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody ItemCreateDto itemDto,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return itemClient.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable(name = "id") int itemId,
            @RequestBody ItemCreateDto itemDto,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return itemClient.update(itemId, itemDto, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(@RequestParam String text,
                                        @RequestParam(defaultValue = "0") @Min(value = 0) int from,
                                        @RequestParam(defaultValue = "10") @Range(min = 1, max = 20) int size) {

        return itemClient.findByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @Valid @RequestBody CommentCreateDto commentDto,
            @PathVariable int itemId,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return itemClient.addComment(commentDto, itemId, userId);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleHttpRequest(HttpClientErrorException ex) {
        return new ResponseEntity<>(
                ex.getResponseBodyAsString(),
                ex.getResponseHeaders(),
                ex.getStatusCode());
    }
}
