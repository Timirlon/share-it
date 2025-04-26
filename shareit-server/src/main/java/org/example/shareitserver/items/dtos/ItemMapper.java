package org.example.shareitserver.items.dtos;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.bookings.Booking;
import org.example.shareitserver.bookings.BookingStatus;
import org.example.shareitserver.bookings.dtos.BookingBaseReadDto;
import org.example.shareitserver.bookings.dtos.BookingReadDto;
import org.example.shareitserver.items.Item;
import org.example.shareitserver.items.comments.dtos.CommentMapper;
import org.example.shareitserver.users.dtos.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

        LocalDateTime now = LocalDateTime.now();


        item.getBookings().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.APPROVED)
                .filter(booking -> booking.getEndDate().isBefore(now))
                .max((booking1, booking2) -> {
                    if (booking1.getEndDate().isAfter(booking2.getEndDate())) {
                        return 1;
                    } else if (booking1.getEndDate().isBefore(booking2.getEndDate())) {
                        return -1;
                    } else {
                        return 0;
                    }
                }).ifPresent(booking -> dto.setLastBooking(
                toDto(booking)));


        item.getBookings().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.APPROVED)
                .filter(booking -> booking.getStartDate().isAfter(now))
                .max((booking1, booking2) -> {
                    if (booking1.getStartDate().isBefore(booking2.getStartDate())) {
                        return 1;
                    } else if (booking1.getStartDate().isAfter(booking2.getStartDate())) {
                        return -1;
                    } else {
                        return 0;
                    }
                }).ifPresent(booking -> dto.setNextBooking(
                        toDto(booking)));


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
