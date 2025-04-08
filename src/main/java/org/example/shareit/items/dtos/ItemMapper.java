package org.example.shareit.items.dtos;

import lombok.RequiredArgsConstructor;
import org.example.shareit.items.Item;
import org.example.shareit.items.comments.dtos.CommentMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final CommentMapper commentMapper;

    public ItemReadDto toDto(Item item) {
        ItemReadDto dto = new ItemReadDto();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwner(item.getOwner().getName());
        dto.setComments(commentMapper.toDto(item.getComments()));

        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }

        return dto;
    }

    public Item fromDto(ItemCreateDto dto) {
        Item item = new Item();

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return item;
    }

    public List<ItemReadDto> toDto(List<Item> items) {
        return items.stream()
                .map(this::toDto)
                .toList();
    }

    public List<ItemReadDto> toDto(Page<Item> items) {
        return items.stream()
                .map(this::toDto)
                .toList();
    }
}
