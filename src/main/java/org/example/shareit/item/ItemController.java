package org.example.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import static org.example.shareit.utils.RequestConstants.USER_ID_REQUEST_HEADER;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        return itemService.findAll(userId);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable int id) {
        return itemService.findById(id);
    }

    @PostMapping
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
    public List<ItemDto> findByText(@RequestParam String text) {
        return itemService.findByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentReadDto addComment(
            @Valid @RequestBody CommentCreateDto comment,
            @PathVariable int itemId,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return itemService.addComment(comment, itemId, userId);
    }
}
