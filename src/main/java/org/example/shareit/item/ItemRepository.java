package org.example.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwnerId(int ownerId);

    List<Item> findAllByNameContainingIgnoreCase(String subStr);
}
