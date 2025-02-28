package org.example.shareit.item;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemMapper {
    public ItemDto toDto(Item item) {
        ItemDto dto = new ItemDto();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        dto.setOwner(item.getOwner().getName());

        return dto;
    }

    public Item fromDto(ItemDto dto) {
        Item item = new Item();

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) item.setAvailable(dto.getAvailable());

        return item;
    }

    public List<ItemDto> toDto(List<Item> items) {
        return items.stream()
                .map(this::toDto)
                .toList();
    }
}
