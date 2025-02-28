package org.example.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.example.shareit.exception.NotFoundException;
import org.example.shareit.item.Item;
import org.example.shareit.item.ItemRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemRepository itemRepository;

    public BookingReadDto toReadDto(Booking booking) {
        BookingReadDto dto = new BookingReadDto();

        dto.setId(booking.getId());
        dto.setStartDate(booking.getStartDate().toString());
        dto.setEndDate(booking.getEndDate().toString());
        dto.setItemName(booking.getItem().getName());
        dto.setBookerName(booking.getBooker().getName());
        dto.setStatus(booking.getStatus().getDisplayName());


        return dto;
    }

    public Booking fromCreateDto(BookingCreateDto dto) {
        Booking booking = new Booking();

        Item bookedItem = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Товар не найден."));

        booking.setItem(bookedItem);
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setStatus(BookingStatus.WAITING);


        return booking;
    }

    public List<BookingReadDto> toReadDto(Collection<Booking> bookings) {
        return bookings.stream()
                .map(this::toReadDto)
                .toList();
    }
}
