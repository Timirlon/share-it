package org.example.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import static org.example.shareit.utils.RequestConstants.USER_ID_REQUEST_HEADER;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(USER_ID_REQUEST_HEADER) int userId,
                                 @RequestParam(defaultValue = "0") @Min(value = 0) int from,
                                 @RequestParam(defaultValue = "10") @Range(min = 1, max = 20) int size) {
        return itemService.findAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable int id) {
        return itemService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(
            @Valid @RequestBody ItemDto item,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        return itemService.create(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @PathVariable(name = "id") int itemId,
            @RequestBody ItemDto item,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        return itemService.update(itemId, item, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text,
                                    @RequestParam(defaultValue = "0") @Min(value = 0) int from,
                                    @RequestParam(defaultValue = "10") @Range(min = 1, max = 20) int size) {
        return itemService.findByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentReadDto addComment(
            @Valid @RequestBody CommentCreateDto comment,
            @PathVariable int itemId,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return itemService.addComment(comment, itemId, userId);
    }
}
