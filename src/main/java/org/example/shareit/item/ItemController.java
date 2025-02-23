package org.example.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.findAll(userId);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable int id) {
        return itemService.findById(id);
    }

    @PostMapping
    public ItemDto addItem(
            @RequestBody ItemDto item,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.addItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(
            @PathVariable(name = "id") int itemId,
            @RequestBody ItemDto item,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.updateItem(itemId, item, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text) {
        return itemService.findByText(text);
    }
}
