package org.example.shareit.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.shareit.items.comments.Comment;
import org.example.shareit.items.comments.dtos.CommentCreateDto;
import org.example.shareit.items.comments.dtos.CommentMapper;
import org.example.shareit.items.dtos.ItemCreateDto;
import org.example.shareit.items.dtos.ItemMapper;
import org.example.shareit.requests.Request;
import org.example.shareit.users.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.example.shareit.utils.RequestConstants.USER_ID_REQUEST_HEADER;

@WebMvcTest({ItemController.class, ItemMapper.class, CommentMapper.class})
public class ItemControllerTest {
    @MockitoBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    static final String BASE_URL = "/items";


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


        Mockito.when(itemService.findAll(ownerId, from, size))
                .thenReturn(List.of(firstItem, secondItem));


        mockMvc.perform(get(BASE_URL + "?from=" + from + "&size=" + size)
                    .header(USER_ID_REQUEST_HEADER, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize))
                .andExpect(jsonPath("$.[0].id").value(firstItemId))
                .andExpect(jsonPath("$.[0].name").value(firstItemName))
                .andExpect(jsonPath("$.[0].description").value(firstItemDesc))
                .andExpect(jsonPath("$.[0].available").value(isAvailable))
                .andExpect(jsonPath("$.[0].owner").value(ownerName))
                .andExpect(jsonPath("$.[1].id").value(secondItemId))
                .andExpect(jsonPath("$.[1].name").value(secondItemName))
                .andExpect(jsonPath("$.[1].description").value(secondItemDesc))
                .andExpect(jsonPath("$.[1].available").value(isAvailable))
                .andExpect(jsonPath("$.[1].owner").value(ownerName));
    }

    @Test
    @SneakyThrows
    void findByIdTest() {
        String ownerName = "get-test-owner-name-1";
        User owner = new User();
        owner.setName(ownerName);

        int itemId = 1;
        String itemName = "get-test-item-name-1";
        String itemDesc = "get-test-item-desc-1";
        boolean isAvailable = true;

        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);
        item.setDescription(itemDesc);
        item.setAvailable(isAvailable);
        item.setOwner(owner);


        Mockito.when(itemService.findById(itemId))
                .thenReturn(item);


        mockMvc.perform(get(BASE_URL + "/" + itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value(itemName))
                .andExpect(jsonPath("$.description").value(itemDesc))
                .andExpect(jsonPath("$.available").value(isAvailable))
                .andExpect(jsonPath("$.owner").value(ownerName));
    }

    @Test
    @SneakyThrows
    void createWithRequestTest() {
        int ownerId = 1;
        String ownerName = "create-test-owner-name-1";
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);

        int requestId = 1;
        String requestDesc = "create-test-request-desc-1";
        Request request = new Request();
        request.setId(requestId);
        request.setDescription(requestDesc);
        request.setCreated(LocalDateTime.now());

        int itemId = 1;
        String itemName = "create-test-item-name-1";
        String itemDesc = "create-test-item-desc-1";
        boolean isAvailable = true;

        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);
        item.setDescription(itemDesc);
        item.setAvailable(isAvailable);
        item.setOwner(owner);
        item.setRequest(request);

        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName(itemName);
        itemDto.setDescription(itemDesc);
        itemDto.setAvailable(isAvailable);
        itemDto.setRequestId(requestId);

        Mockito.when(itemService.create(
                Mockito.any(Item.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(item);

        String itemInJson = objectMapper.writeValueAsString(itemDto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(itemInJson)
                        .header(USER_ID_REQUEST_HEADER, ownerId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value(itemName))
                .andExpect(jsonPath("$.description").value(itemDesc))
                .andExpect(jsonPath("$.available").value(isAvailable))
                .andExpect(jsonPath("$.owner").value(ownerName))
                .andExpect(jsonPath("$.requestId").value(requestId));
    }

    @Test
    @SneakyThrows
    void createWithoutRequestTest() {
        int ownerId = 1;
        String ownerName = "create-test-owner-name-1";
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);

        int itemId = 1;
        String itemName = "create-test-item-name-1";
        String itemDesc = "create-test-item-desc-1";
        boolean isAvailable = true;

        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);
        item.setDescription(itemDesc);
        item.setAvailable(isAvailable);
        item.setOwner(owner);

        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName(itemName);
        itemDto.setDescription(itemDesc);
        itemDto.setAvailable(isAvailable);

        Mockito.when(itemService.create(
                        Mockito.any(Item.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(item);

        String itemInJson = objectMapper.writeValueAsString(itemDto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(itemInJson)
                        .header(USER_ID_REQUEST_HEADER, ownerId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value(itemName))
                .andExpect(jsonPath("$.description").value(itemDesc))
                .andExpect(jsonPath("$.available").value(isAvailable))
                .andExpect(jsonPath("$.owner").value(ownerName))
                .andExpect(jsonPath("$.requestId").doesNotExist());
    }

    @Test
    @SneakyThrows
    void updateTest() {
        int ownerId = 1;
        String ownerName = "create-test-owner-name-1";
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);

        int itemId = 1;
        String itemName = "create-test-item-name-1";
        String itemDesc = "create-test-item-desc-1";
        boolean isAvailable = true;

        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);
        item.setDescription(itemDesc);
        item.setAvailable(isAvailable);
        item.setOwner(owner);

        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName(itemName);
        itemDto.setDescription(itemDesc);

        Mockito.when(itemService.update(
                        Mockito.anyInt(), Mockito.any(Item.class), Mockito.anyInt()))
                .thenReturn(item);

        String itemInJson = objectMapper.writeValueAsString(itemDto);

        mockMvc.perform(patch("/items/" + itemId)
                        .contentType(APPLICATION_JSON)
                        .content(itemInJson)
                        .header(USER_ID_REQUEST_HEADER, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value(itemName))
                .andExpect(jsonPath("$.description").value(itemDesc))
                .andExpect(jsonPath("$.available").value(isAvailable))
                .andExpect(jsonPath("$.owner").value(ownerName));
    }

    @Test
    @SneakyThrows
    void findAllByTextTest() {
        int expectedSize = 2;
        String text = "get";
        int from = 0;
        int size = 10;

        int ownerId = 1;
        String ownerName = "create-test-owner-name-1";
        User owner = new User();
        owner.setId(ownerId);
        owner.setName(ownerName);

        int firstItemId = 1;
        String firstItemName = "create-test-item-name-1";
        String firstItemDesc = "create-test-item-desc-1";
        boolean isAvailable = true;
        Item firstItem = new Item();
        firstItem.setId(firstItemId);
        firstItem.setName(firstItemName);
        firstItem.setDescription(firstItemDesc);
        firstItem.setAvailable(isAvailable);
        firstItem.setOwner(owner);

        int secondItemId = 2;
        String secondItemName = "create-test-item-name-1";
        String secondItemDesc = "create-test-item-desc-1";
        Item secondItem = new Item();
        secondItem.setId(secondItemId);
        secondItem.setName(secondItemName);
        secondItem.setDescription(secondItemDesc);
        secondItem.setAvailable(isAvailable);
        secondItem.setOwner(owner);


        Mockito.when(itemService.findByText(text, from, size))
                .thenReturn(new PageImpl<>(
                        List.of(firstItem, secondItem)));


        mockMvc.perform(get("/items/search?text=" + text
                + "&from=" + from
                + "&size=" + size))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expectedSize))
                .andExpect(jsonPath("$.[0].id").value(firstItemId))
                .andExpect(jsonPath("$.[0].name").value(firstItemName))
                .andExpect(jsonPath("$.[0].description").value(firstItemDesc))
                .andExpect(jsonPath("$.[0].available").value(isAvailable))
                .andExpect(jsonPath("$.[0].owner").value(ownerName))
                .andExpect(jsonPath("$.[1].id").value(secondItemId))
                .andExpect(jsonPath("$.[1].name").value(secondItemName))
                .andExpect(jsonPath("$.[1].description").value(secondItemDesc))
                .andExpect(jsonPath("$.[1].available").value(isAvailable))
                .andExpect(jsonPath("$.[1].owner").value(ownerName));
    }

    @Test
    @SneakyThrows
    void addCommentTest() {
        int itemId = 1;
        String itemName = "create-test-item-name-1";
        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);

        int authorId = 1;
        String authorName = "create-test-author-name-1";
        User author = new User();
        author.setId(authorId);
        author.setName(authorName);

        int commentId = 1;
        String commentText = "create-test-comment-text-1";
        LocalDateTime now = LocalDateTime.now();

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setText(commentText);
        comment.setAuthor(author);
        comment.setCreated(now);
        comment.setItem(item);

        CommentCreateDto commentDto = new CommentCreateDto();
        commentDto.setText(commentText);


        Mockito.when(itemService.addComment(Mockito.any(
                Comment.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(comment);


        String commentAsJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                    .contentType(APPLICATION_JSON)
                    .content(commentAsJson)
                    .header(USER_ID_REQUEST_HEADER, authorId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.text").value(commentText))
                .andExpect(jsonPath("$.itemName").value(itemName))
                .andExpect(jsonPath("$.authorName").value(authorName))
                .andExpect(jsonPath("$.created").exists());
    }
}
