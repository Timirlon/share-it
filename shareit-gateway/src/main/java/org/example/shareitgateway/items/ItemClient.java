package org.example.shareitgateway.items;

import org.example.shareitserver.items.comments.dtos.CommentCreateDto;
import org.example.shareitserver.items.dtos.ItemCreateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

import static org.example.shareitgateway.utils.HeaderUtils.getUserIdRequestHeader;

@Component
public class ItemClient {
    private final RestTemplate restTemplate;

    public ItemClient(@Value("${shareitserver.server.url}") String url,
                      RestTemplateBuilder builder) {

        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .build();
    }

    public ResponseEntity<Object> findAll(int userId, int from, int size) {
        HttpHeaders header = getUserIdRequestHeader(userId);

        return restTemplate.exchange(
                "/items?from={from}&size={size}",
                HttpMethod.GET,
                new HttpEntity<>(null, header),
                Object.class,
                Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> findById(int id) {
        return restTemplate.getForEntity(
                "/items/{id}",
                Object.class,
                Map.of("id", id));
    }

    public ResponseEntity<Object> create(ItemCreateDto itemDto, int userId) {
        HttpHeaders header = getUserIdRequestHeader(userId);

        return restTemplate.exchange(
                "/items",
                HttpMethod.POST,
                new HttpEntity<>(itemDto, header),
                Object.class);
    }

    public ResponseEntity<Object> update(int itemId, ItemCreateDto itemDto, int userId) {
        HttpHeaders header = getUserIdRequestHeader(userId);

        return restTemplate.exchange(
                "/items/{id}",
                HttpMethod.PATCH,
                new HttpEntity<>(itemDto, header),
                Object.class,
                Map.of("id", itemId));
    }

    public ResponseEntity<Object> findByText(String text, int from, int size) {
        return restTemplate.exchange(
                "/items/search?text={text}&from={from}&size={size}",
                HttpMethod.GET,
                null,
                Object.class,
                Map.of("text", text, "from", from, "size", size));
    }

    public ResponseEntity<Object> addComment(
            CommentCreateDto commentDto, int itemId, int userId) {

        HttpHeaders header = getUserIdRequestHeader(userId);

        return restTemplate.exchange(
                "/items/{itemId}/comment",
                HttpMethod.POST,
                new HttpEntity<>(commentDto, header),
                Object.class,
                Map.of("itemId", itemId));
    }
}
