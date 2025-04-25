package org.example.shareitgateway.requests;

import org.example.shareitserver.requests.dtos.RequestCreateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

import static org.example.shareitgateway.utils.HeaderUtils.getUserIdRequestHeader;

@Component
public class RequestClient {
    private final RestTemplate restTemplate;

    public RequestClient(@Value("${shareitserver.server.url}") String url,
                      RestTemplateBuilder builder) {

        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .build();
    }

    public ResponseEntity<Object> create(RequestCreateDto requestDto, int userId) {
        HttpHeaders header = getUserIdRequestHeader(userId);

        return restTemplate.exchange(
                "/requests",
                HttpMethod.POST,
                new HttpEntity<>(requestDto, header),
                Object.class);
    }

    public ResponseEntity<Object> findAllOfMine(int userId) {
        HttpHeaders header = getUserIdRequestHeader(userId);

        return restTemplate.exchange(
                "requests",
                HttpMethod.GET,
                new HttpEntity<>(null, header),
                Object.class);
    }

    public ResponseEntity<Object> findAllOfOthers(int userId, int from, int size) {
        HttpHeaders header = getUserIdRequestHeader(userId);

        return restTemplate.exchange(
                "requests/all",
                HttpMethod.GET,
                new HttpEntity<>(null, header),
                Object.class,
                Map.of("from", from, "size", size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(int requestId) {
        return restTemplate.getForEntity(
                "requests/{requestId}",
                Object.class,
                Map.of("requestId", requestId));
    }
}
