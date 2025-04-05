package org.example.shareit.items;

import lombok.SneakyThrows;
import org.example.shareit.bookings.BookingRepository;
import org.example.shareit.exceptions.NotFoundException;
import org.example.shareit.items.comments.CommentRepository;
import org.example.shareit.requests.Request;
import org.example.shareit.requests.RequestRepository;
import org.example.shareit.users.User;
import org.example.shareit.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    RequestRepository requestRepository;

    @InjectMocks
    ItemService itemService;


    @Test
    @SneakyThrows
    void findAllTest() {
        int expectedSize = 2;
        int from = 0;
        int size = 5;

        int ownerId = 1;
        String ownerName = "get-test-owner-name-1";
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);

        boolean isAvailable = true;

        int firstItemId = 1;
        String firstItemName = "get-test-item-name-1";
        String firstItemDesc = "get-test-item-desc-1";
        Item firstItem = new Item();
        firstItem.setId(firstItemId);
        firstItem.setName(firstItemName);
        firstItem.setDescription(firstItemDesc);
        firstItem.setAvailable(isAvailable);
        firstItem.setOwner(owner);

        int secondItemId = 2;
        String secondItemName = "get-test-item-name-2";
        String secondItemDesc = "get-test-item-desc-2";
        Item secondItem = new Item();
        secondItem.setId(secondItemId);
        secondItem.setName(secondItemName);
        secondItem.setDescription(secondItemDesc);
        secondItem.setAvailable(isAvailable);
        secondItem.setOwner(owner);


        Mockito.when(itemRepository.findAllByOwnerId(
                        Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(firstItem, secondItem));


        List<Item> foundItems = itemService.findAll(ownerId, from, size);


        assertEquals(expectedSize, foundItems.size());

        assertEquals(firstItemId, foundItems.get(0).getId());
        assertEquals(firstItemName, foundItems.get(0).getName());
        assertEquals(firstItemDesc, foundItems.get(0).getDescription());
        assertEquals(ownerId, foundItems.get(0).getOwner().getId());
        assertTrue(foundItems.get(0).getAvailable());

        assertEquals(secondItemId, foundItems.get(1).getId());
        assertEquals(secondItemName, foundItems.get(1).getName());
        assertEquals(secondItemDesc, foundItems.get(1).getDescription());
        assertEquals(ownerId, foundItems.get(1).getOwner().getId());
        assertTrue(foundItems.get(1).getAvailable());
    }

    @Test
    @SneakyThrows
    void findByIdTestSuccess() {
        int itemId = 1;
        String itemName = "get-test-item-name-1";
        String itemDesc = "get-test-item-desc-1";
        boolean isAvailable = true;
        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);
        item.setDescription(itemDesc);
        item.setAvailable(isAvailable);

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));


        Item foundItem = itemService.findById(itemId);


        assertEquals(itemId, foundItem.getId());
        assertEquals(itemName, foundItem.getName());
        assertEquals(itemDesc, foundItem.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    @SneakyThrows
    void findByIdTestItemNotFoundFail() {
        String expectedMessage = "Товар не найден.";
        int wrongId = 999;


        Mockito.when(itemRepository.findById(wrongId))
                .thenReturn(Optional.empty());


        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.findById(wrongId));

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void createWithoutRequestTestSuccess() {
        int ownerId = 1;
        String ownerName = "create-test-owner-name-1";
        String ownerEmail = "create-test-owner-email-1";
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);
        owner.setEmail(ownerEmail);


        String itemName = "create-test-item-name-1";
        String itemDesc = "create-test-item-desc-1";
        boolean isAvailable = true;
        Item item = new Item();
        item.setName(itemName);
        item.setDescription(itemDesc);
        item.setAvailable(isAvailable);


        Mockito.when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));


        Item createdItem = itemService.create(item, ownerId, 0);


        assertEquals(itemName, createdItem.getName());
        assertEquals(itemDesc, createdItem.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(ownerId, createdItem.getOwner().getId());
    }

    @Test
    @SneakyThrows
    void createWithRequestTestSuccess() {
        int ownerId = 1;
        String ownerName = "create-test-owner-name-1";
        String ownerEmail = "create-test-owner-email-1";
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);
        owner.setEmail(ownerEmail);

        int requestId = 1;
        String reqDesc = "create-test-request-description-1";
        LocalDateTime now = LocalDateTime.now();
        Request request = new Request();
        request.setId(requestId);
        request.setDescription(reqDesc);
        request.setCreated(now);
        request.setRequester(owner);

        String itemName = "create-test-item-name-1";
        String itemDesc = "create-test-item-desc-1";
        boolean isAvailable = true;
        Item item = new Item();
        item.setName(itemName);
        item.setDescription(itemDesc);
        item.setAvailable(isAvailable);


        Mockito.when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));


        Mockito.when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(request));


        Item createdItem = itemService.create(item, ownerId, requestId);


        assertEquals(itemName, createdItem.getName());
        assertEquals(itemDesc, createdItem.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(ownerId, createdItem.getOwner().getId());
        assertEquals(requestId, createdItem.getRequest().getId());
    }

    @Test
    @SneakyThrows
    void createTestUserNotFoundFail() {
        String expectedMessage = "Пользователь не найден.";
        int wrongUserId = 999;

        String itemName = "create-test-item-name-1";
        String itemDesc = "create-test-item-desc-1";
        boolean isAvailable = true;
        Item item = new Item();
        item.setName(itemName);
        item.setDescription(itemDesc);
        item.setAvailable(isAvailable);


        Mockito.when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());


        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.create(item, wrongUserId, 0));

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void createTestRequestNotFoundFail() {
        int wrongRequestId = 999;
        String expectedMessage = "Запрос не найден.";

        int ownerId = 1;
        String ownerName = "create-test-owner-name-1";
        String ownerEmail = "create-test-owner-email-1";
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);
        owner.setEmail(ownerEmail);


        String itemName = "create-test-item-name-1";
        String itemDesc = "create-test-item-desc-1";
        boolean isAvailable = true;
        Item item = new Item();
        item.setName(itemName);
        item.setDescription(itemDesc);
        item.setAvailable(isAvailable);


        Mockito.when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        Mockito.when(requestRepository.findById(wrongRequestId))
                .thenReturn(Optional.empty());


        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.create(item, ownerId, wrongRequestId)
        );

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void updateItemNameTestSuccess() {
        int ownerId = 1;
        String ownerName = "update-test-owner-name-1";
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);

        int itemId = 1;
        String initialName = "update-test-item-name-1";
        String initialDesc = "update-test-item-desc-1";
        boolean initialAvailability = true;
        String updatedName = "update-test-item-name-2";

        Item initialItem = new Item();
        initialItem.setId(itemId);
        initialItem.setName(initialName);
        initialItem.setDescription(initialDesc);
        initialItem.setAvailable(initialAvailability);
        initialItem.setOwner(owner);

        Item updateItem = new Item();
        updateItem.setName(updatedName);

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(initialItem));


        Item resultItem = itemService.update(itemId, updateItem, ownerId);

        assertEquals(itemId, resultItem.getId());
        assertEquals(updatedName, resultItem.getName());
        assertEquals(initialDesc, resultItem.getDescription());
        assertEquals(initialAvailability, resultItem.getAvailable());
        assertEquals(ownerId, resultItem.getOwner().getId());
    }

    @Test
    @SneakyThrows
    void updateItemDescTestSuccess() {
        int ownerId = 1;
        String ownerName = "update-test-owner-name-1";
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);

        int itemId = 1;
        String initialName = "update-test-item-name-1";
        String initialDesc = "update-test-item-desc-1";
        boolean initialAvailability = true;
        String updatedDesc = "update-test-item-desc-2";

        Item initialItem = new Item();
        initialItem.setId(itemId);
        initialItem.setName(initialName);
        initialItem.setDescription(initialDesc);
        initialItem.setAvailable(initialAvailability);
        initialItem.setOwner(owner);

        Item updateItem = new Item();
        updateItem.setDescription(updatedDesc);

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(initialItem));


        Item resultItem = itemService.update(itemId, updateItem, ownerId);

        assertEquals(itemId, resultItem.getId());
        assertEquals(initialName, resultItem.getName());
        assertEquals(updatedDesc, resultItem.getDescription());
        assertEquals(initialAvailability, resultItem.getAvailable());
        assertEquals(ownerId, resultItem.getOwner().getId());
    }

    @Test
    @SneakyThrows
    void updateItemAvailabilityTestSuccess() {
        int ownerId = 1;
        String ownerName = "update-test-owner-name-1";
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);

        int itemId = 1;
        String initialName = "update-test-item-name-1";
        String initialDesc = "update-test-item-desc-1";
        boolean initialAvailability = true;
        boolean updatedAvailability = false;

        Item initialItem = new Item();
        initialItem.setId(itemId);
        initialItem.setName(initialName);
        initialItem.setDescription(initialDesc);
        initialItem.setAvailable(initialAvailability);
        initialItem.setOwner(owner);

        Item updateItem = new Item();
        updateItem.setAvailable(updatedAvailability);

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(initialItem));


        Item resultItem = itemService.update(itemId, updateItem, ownerId);

        assertEquals(itemId, resultItem.getId());
        assertEquals(initialName, resultItem.getName());
        assertEquals(initialDesc, resultItem.getDescription());
        assertEquals(updatedAvailability, resultItem.getAvailable());
        assertEquals(ownerId, resultItem.getOwner().getId());
    }

    @Test
    @SneakyThrows
    void updateTestItemNotFoundFail() {
        String expectedMessage = "Товар не найден.";
        int wrongId = 999;
        int ownerId = 0;
        Item item = new Item();
        item.setName("update-test-item-name-1");


        Mockito.when(itemRepository.findById(wrongId))
                .thenReturn(Optional.empty());


        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.update(wrongId, item, ownerId));

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void updateTestUserIsNotOwnerFail() {
        String expectedMessage = "Товар не найден.";

        int userId = 999;

        int ownerId = 1;
        String ownerName = "update-test-owner-name-1";
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);

        int itemId = 1;
        String initialName = "update-test-item-name-1";
        String initialDesc = "update-test-item-desc-1";
        boolean initialAvailability = true;
        String updatedName = "update-test-item-name-2";

        Item initialItem = new Item();
        initialItem.setId(itemId);
        initialItem.setName(initialName);
        initialItem.setDescription(initialDesc);
        initialItem.setAvailable(initialAvailability);
        initialItem.setOwner(owner);

        Item updateItem = new Item();
        updateItem.setName(updatedName);

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(initialItem));


        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> itemService.update(itemId, initialItem, userId));

        assertEquals(expectedMessage, e.getMessage());
    }
}
