package org.example.shareit.item;

import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareit.booking.Booking;
import org.example.shareit.booking.BookingRepository;
import org.example.shareit.booking.BookingStatus;
import org.example.shareit.exception.NotFoundException;
import org.example.shareit.requests.Request;
import org.example.shareit.requests.RequestRepository;
import org.example.shareit.user.User;
import org.example.shareit.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    RequestRepository requestRepository;

    ItemMapper itemMapper;
    CommentMapper commentMapper;


    public List<ItemDto> findAll(int ownerId, int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        return itemMapper.toDto(
                itemRepository.findAllByOwnerId(ownerId, pageable));
    }

    public ItemDto findById(int id) {
        return itemMapper.toDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар не найден.")));
    }

    public ItemDto create(ItemDto itemDto, int userId) {
        Item item = itemMapper.fromDto(itemDto);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            Request request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));

            item.setRequest(request);
        }

        return itemMapper.toDto(itemRepository.save(item));
    }

    public ItemDto update(int itemId, ItemDto itemDto, int userId) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден"));

        if (oldItem.getOwner().getId() != userId) {
            throw new NotFoundException("Товар не найден");
        }

        Item patchItem = itemMapper.fromDto(itemDto);
        patchItem.setId(itemId);
        patchItem.setOwner(oldItem.getOwner());

        if (patchItem.getName() == null) {
            patchItem.setName(oldItem.getName());
        }

        if (patchItem.getDescription() == null) {
            patchItem.setDescription(oldItem.getDescription());
        }

        if (itemDto.getAvailable() == null) {
            patchItem.setAvailable(oldItem.isAvailable());
        }


        return itemMapper.toDto(itemRepository.save(patchItem));
    }

    public List<ItemDto> findByText(String text, int from, int size) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        return itemMapper.toDto(itemRepository.findAllByText(text, pageable));
    }

    public CommentReadDto addComment(CommentCreateDto commentDto, int itemId, int authorId) {
        Comment comment = commentMapper.fromDto(commentDto);

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));

        Booking booking = bookingRepository.findAllByBooker_IdAndItem_IdAndStatusAndStartDateBeforeOrderByStartDateAsc(
                authorId, itemId, BookingStatus.APPROVED, LocalDateTime.now()).stream()
                .findFirst()
                .orElseThrow(() -> new ValidationException("Вы не можете оставлять отзыв на этот товар."));


        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        return commentMapper.toDto(commentRepository.save(comment));
    }
}
