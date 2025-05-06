package org.example.shareitserver.items;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.items.comments.Comment;
import org.example.shareitserver.items.comments.dtos.CommentCreateDto;
import org.example.shareitserver.items.comments.dtos.CommentMapper;
import org.example.shareitserver.items.comments.dtos.CommentReadDto;
import org.example.shareitserver.items.dtos.ItemCreateDto;
import org.example.shareitserver.items.dtos.ItemReadDto;
import org.example.shareitserver.items.dtos.ItemMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import static org.example.shareitserver.utils.RequestConstants.USER_ID_REQUEST_HEADER;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@RestController
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;
    ItemMapper itemMapper;
    CommentMapper commentMapper;

    @GetMapping
    public List<ItemReadDto> findAllByOwnerId(@RequestHeader(USER_ID_REQUEST_HEADER) int userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        return itemMapper.toDto(
                itemService.findAllByOwnerId(userId, from, size));
    }

    @GetMapping("/{id}")
    public ItemReadDto findById(@PathVariable(name = "id") int itemId,
                                @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

        return itemMapper.toDto(
                itemService.findById(itemId, userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemReadDto create(
            @RequestBody ItemCreateDto itemDto,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        Item item = itemMapper.fromDto(itemDto);
        int requestId = itemDto.getRequestId();


        return itemMapper.toDto(
                itemService.create(item, userId, requestId));
    }

    @PatchMapping("/{id}")
    public ItemReadDto update(
            @PathVariable(name = "id") int itemId,
            @RequestBody ItemCreateDto itemDto,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        Item item = itemMapper.fromDto(itemDto);

        return itemMapper.toDto(
                itemService.update(itemId, item, userId));
    }

    @GetMapping("/search")
    public List<ItemReadDto> findByText(@RequestParam String text,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        return itemMapper.toDto(
                itemService.findByText(text, from, size, userId));
    }

    @PostMapping("/{itemId}/comment")
    public CommentReadDto addComment(
            @RequestBody CommentCreateDto commentDto,
            @PathVariable int itemId,
            @RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        Comment comment = commentMapper.fromDto(commentDto);

        return commentMapper.toDto(
                itemService.addComment(comment, itemId, userId));
    }
}
