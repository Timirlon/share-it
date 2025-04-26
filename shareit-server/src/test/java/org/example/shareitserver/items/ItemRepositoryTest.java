package org.example.shareitserver.items;

import lombok.SneakyThrows;
import org.example.shareitserver.users.User;
import org.example.shareitserver.users.UserRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @SneakyThrows
    void findAllByOwnerIdTest() {
        int expectedSize = 1;

        User owner = new User();
        owner.setName("test-owner-name");
        owner.setEmail("test-owner-email");

        userRepository.save(owner);


        Item firstItem = new Item();
        firstItem.setName("test-item-name");
        firstItem.setDescription("test-item-desc");
        firstItem.setAvailable(true);
        firstItem.setOwner(owner);

        Item secondItem = new Item();
        secondItem.setName("new-item-name");
        secondItem.setDescription("new-item-desc");
        secondItem.setAvailable(true);

        itemRepository.save(firstItem);
        itemRepository.save(secondItem);


        List<Item> found = itemRepository.findAllByOwnerId_OrderById(
                owner.getId(), PageRequest.of(0, 5));


        assertEquals(expectedSize, found.size());
        assertEquals(firstItem.getId(), found.get(0).getId());
        assertEquals(firstItem.getName(), found.get(0).getName());
        assertEquals(firstItem.getDescription(), found.get(0).getDescription());
        assertTrue(found.get(0).getAvailable());
        assertEquals(owner.getId(), found.get(0).getOwner().getId());
    }

    @Test
    @SneakyThrows
    void findAllByTextTest() {
        int expectedSize = 1;
        String text = "test";

        User owner = new User();
        owner.setName("test-owner-name");
        owner.setEmail("test-owner-email");

        userRepository.save(owner);


        Item firstItem = new Item();
        firstItem.setName("test-item-name");
        firstItem.setDescription("test-item-desc");
        firstItem.setAvailable(true);
        firstItem.setOwner(owner);

        Item secondItem = new Item();
        secondItem.setName("new-item-name");
        secondItem.setDescription("new-item-desc");
        secondItem.setAvailable(true);
        secondItem.setOwner(owner);

        itemRepository.save(firstItem);
        itemRepository.save(secondItem);


        List<Item> found = itemRepository.findAllByText(
                text, PageRequest.of(0, 5))
                .toList();


        assertEquals(expectedSize, found.size());
        assertEquals(firstItem.getId(), found.get(0).getId());
        assertEquals(firstItem.getName(), found.get(0).getName());
        assertEquals(firstItem.getDescription(), found.get(0).getDescription());
        assertTrue(found.get(0).getAvailable());
        assertEquals(owner.getId(), found.get(0).getOwner().getId());
    }
}
