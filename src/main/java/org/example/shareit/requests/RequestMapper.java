package org.example.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.example.shareit.item.ItemDto;
import org.example.shareit.item.ItemMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    ItemMapper itemMapper;

    public RequestReadDto toDto(Request request) {
        RequestReadDto dto = new RequestReadDto();

        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());


        if (!request.getResponseItems().isEmpty()) {
            List<ItemDto> itemDtos = itemMapper.toDto(request.getResponseItems());
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
}
