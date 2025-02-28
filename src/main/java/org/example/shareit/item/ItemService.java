package org.example.shareit.item;

import lombok.RequiredArgsConstructor;
import org.example.shareit.exception.NotFoundException;
import org.example.shareit.user.User;
import org.example.shareit.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper mapper;
    private final UserRepository userRepository;

    public List<ItemDto> findAll(int ownerId) {
        return mapper.toDto(itemRepository.findAllByOwnerId(ownerId));
    }

    public ItemDto findById(int id) {
        return mapper.toDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар не найден.")));
    }

    public ItemDto create(ItemDto itemDto, int userId) {
        Item item = mapper.fromDto(itemDto);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        item.setOwner(owner);


        return mapper.toDto(itemRepository.save(item));
    }

    public ItemDto update(int itemId, ItemDto itemDto, int userId) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден"));

        if (oldItem.getOwner().getId() != userId) {
            throw new NotFoundException("Товар не найден");
        }

        Item patchItem = mapper.fromDto(itemDto);
        patchItem.setId(itemId);
        patchItem.setOwner(oldItem.getOwner());

        if (patchItem.getName() == null) {
            patchItem.setName(oldItem.getName());
        }

        if (patchItem.getDescription() == null) {
            patchItem.setDescription(oldItem.getDescription());
        }

        if (itemDto.getAvailable() == null) {
            patchItem.setAvailable(oldItem.isAvailable());
        }


        return mapper.toDto(itemRepository.save(patchItem));
    }

    public List<ItemDto> findByText(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }

        return mapper.toDto(itemRepository.findAllByNameContainingIgnoreCase(text));
    }
}
