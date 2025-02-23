package org.example.shareit.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.example.shareit.exception.NotFoundException;
import org.example.shareit.user.User;
import org.example.shareit.user.UserInMemoryDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemInMemoryDao itemDao;
    private final ItemMapper mapper;
    private final UserInMemoryDao userDao;

    public List<ItemDto> findAll(int ownerId) {
        return itemDao.findAll().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(mapper::toDto)
                .toList();
    }

    public ItemDto findById(int id) {
        return mapper.toDto(itemDao.findById(id));
    }

    public ItemDto addItem(ItemDto dto, int userId) {
        if (dto.getName() == null || dto.getName().isEmpty() || dto.getDescription() == null
                || dto.getDescription().isEmpty() || dto.getAvailable() == null) {
            throw new ValidationException("Недействительные учетные данные товара.");
        }

        Item item = mapper.fromDto(dto);

        User owner = userDao.findById(userId);
        if (owner == null) {
            throw new NotFoundException("Пользователь не найден.");
        }

        item.setOwner(owner);

        return mapper.toDto(itemDao.insert(item));
    }

    public ItemDto updateItem(int itemId, ItemDto dto, int userId) {
        Item oldItem = itemDao.findById(itemId);

        if (oldItem == null || oldItem.getOwner().getId() != userId) {
            throw new NotFoundException("Товар не найден");
        }

        Item patchItem = mapper.fromDto(dto);
        patchItem.setId(itemId);
        patchItem.setOwner(oldItem.getOwner());
        if (patchItem.getName() == null) patchItem.setName(oldItem.getName());
        if (patchItem.getDescription() == null) patchItem.setDescription(oldItem.getDescription());
        if (dto.getAvailable() == null) patchItem.setAvailable(oldItem.isAvailable());


        return mapper.toDto(itemDao.insert(patchItem));
    }

    public List<ItemDto> findByText(String text) {
        if (text == null || text.isEmpty()) return List.of();

        return itemDao.findAll().stream()
                .filter(Item::isAvailable)
                .filter(item -> {
                    String subStr = text.toLowerCase();
                    String lowerName = item.getName().toLowerCase();
                    String lowerDesc = item.getDescription().toLowerCase();

                    return lowerName.contains(subStr) || lowerDesc.contains(subStr);
                })
                .map(mapper::toDto)
                .toList();
    }

    private boolean checkIfItemExistsById(int itemId) {
        return itemDao.findById(itemId) != null;
    }
}
