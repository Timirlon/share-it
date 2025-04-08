package org.example.shareit.requests.dtos;

import lombok.RequiredArgsConstructor;
import org.example.shareit.items.dtos.ItemReadDto;
import org.example.shareit.items.dtos.ItemMapper;
import org.example.shareit.requests.Request;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    private final ItemMapper itemMapper;

    public RequestReadDto toDto(Request request) {
        RequestReadDto dto = new RequestReadDto();

        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());


        if (!request.getResponseItems().isEmpty()) {
            List<ItemReadDto> itemDtos = itemMapper.toDto(request.getResponseItems());
            dto.setResponseItems(itemDtos);
        }

        return dto;
    }

    public Request fromDto(RequestCreateDto dto) {
        Request request = new Request();

        request.setDescription(dto.getDescription());
        request.setCreated(LocalDateTime.now());


        return request;
    }

    public List<RequestReadDto> toDto(List<Request> requests) {
        return requests.stream()
                .map(this::toDto)
                .toList();
    }

    public List<RequestReadDto> toDto(Page<Request> requestPage) {
        return requestPage.stream()
                .map(this::toDto)
                .toList();
    }
}
