package org.example.shareitgateway.users;

import org.example.shareitserver.users.dtos.UserCreateDto;
import org.example.shareitserver.users.dtos.UserUpdDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;


@Component
public class UserClient {
    private final RestTemplate restTemplate;

    public UserClient(@Value("${shareitserver.server.url}") String url,
                      RestTemplateBuilder builder) {

        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .build();
    }

    public ResponseEntity<Object> findAll() {
        return restTemplate.getForEntity("/users", Object.class);
    }

    public ResponseEntity<Object> findById(int id) {
        return restTemplate.getForEntity("/users/{id}",
                Object.class,
                Map.of("id", id));
    }

    public ResponseEntity<Object> create(UserCreateDto userDto) {
        return restTemplate.postForEntity("/users", userDto, Object.class);
    }

    public ResponseEntity<Object> update(int id, UserUpdDto userDto) {
        return restTemplate.exchange("/users/{id}",
                HttpMethod.PATCH,
                new HttpEntity<>(userDto),
                Object.class,
                Map.of("id", id));
    }

    public ResponseEntity<Object> deleteById(int id) {
        return restTemplate.exchange("users/{id}",
                HttpMethod.DELETE,
                null,
                Object.class,
                Map.of("id", id));
    }
}
