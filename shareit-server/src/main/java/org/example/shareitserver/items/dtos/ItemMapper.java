package org.example.shareitserver.items.dtos;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.bookings.Booking;
import org.example.shareitserver.bookings.dtos.BookingBaseReadDto;
import org.example.shareitserver.items.Item;
import org.example.shareitserver.items.comments.dtos.CommentMapper;
import org.example.shareitserver.users.dtos.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@Component
@RequiredArgsConstructor
public class ItemMapper {
    CommentMapper commentMapper;
    UserMapper userMapper;

    public ItemReadDto toDto(Item item) {
        ItemReadDto dto = new ItemReadDto();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwner(userMapper.toDto(item.getOwner()));
        dto.setComments(commentMapper.toDto(item.getComments()));

        if (item.getNextBooking() != null) {
            dto.setNextBooking(toDto(item.getNextBooking()));
        }

        if (item.getLastBooking() != null) {
            dto.setLastBooking(toDto(item.getLastBooking()));
        }

        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }

        return dto;
    }

    public Item fromDto(ItemCreateDto dto) {
        Item item = new Item();

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return item;
    }

    public List<ItemReadDto> toDto(List<Item> items) {
        return items.stream()
                .map(this::toDto)
                .toList();
    }

    public List<ItemReadDto> toDto(Page<Item> items) {
        return items.stream()
                .map(this::toDto)
                .toList();
    }

    private BookingBaseReadDto toDto(Booking booking) {
        BookingBaseReadDto dto = new BookingBaseReadDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStartDate());
        dto.setEnd(booking.getEndDate());
        dto.setBookerId(booking.getBooker().getId());
        dto.setBookerName(booking.getBooker().getName());
        dto.setStatus(booking.getStatus());


        return dto;
    }

}
