package org.example.shareit.item;

import lombok.RequiredArgsConstructor;
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

    public Item addItem(ItemDto dto, int userId) {
        Item item = mapper.fromDto(dto);

        if (checkIfItemExists(item)) {
            return null;
        }

        item.setOwner(userDao.findById(userId));
        itemDao.insert(item);

        return item;
    }

    public Item updateItem(ItemDto dto, int userId) {
        Item reqItem = mapper.fromDto(dto);
        Item patchItem = itemDao.findById(reqItem.getId());

        if (!checkIfItemExists(reqItem) || patchItem.getOwner().getId() != userId) {
            return null;
        }

        if (reqItem.getName() != null) patchItem.setName(reqItem.getName());
        if (reqItem.getDescription() != null) patchItem.setDescription(reqItem.getDescription());
        patchItem.setAvailable(reqItem.isAvailable());

        itemDao.insert(patchItem);
        return patchItem;
    }

    public List<ItemDto> findByText(String text) {
        return itemDao.findAll().stream()
                .filter(Item::isAvailable)
                .filter(item -> {
                    String subStr = text.toLowerCase();
                    String lowerName = item.getName();
                    String lowerDesc = item.getDescription();

                    return lowerName.contains(subStr) || lowerDesc.contains(subStr);
                })
                .map(mapper::toDto)
                .toList();
    }

    private boolean checkIfItemExists(Item item) {
        return itemDao.findById(item.getId()) != null;
    }
}
