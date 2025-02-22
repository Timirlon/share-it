package org.example.shareit.item;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class ItemInMemoryDao {
    Map<Integer, Item> items = new HashMap<>();

    public Collection<Item> findAll() {
        return items.values();
    }

    public Item findById(int id) {
        return items.get(id);
    }

    public Item insert(Item item) {
        items.put(item.getId(), item);
        return item;
    }
}
