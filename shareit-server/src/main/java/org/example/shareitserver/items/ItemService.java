package org.example.shareitserver.items;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.bookings.Booking;
import org.example.shareitserver.bookings.BookingRepository;
import org.example.shareitserver.bookings.BookingStatus;
import org.example.shareitserver.exceptions.NotFoundException;
import org.example.shareitserver.exceptions.ValidationException;
import org.example.shareitserver.items.comments.*;
import org.example.shareitserver.requests.Request;
import org.example.shareitserver.requests.RequestRepository;
import org.example.shareitserver.users.User;
import org.example.shareitserver.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@Service
public class ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    RequestRepository requestRepository;


    public List<Item> findAll(int ownerId, int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        return itemRepository.findAllByOwnerId_OrderById(ownerId, pageable);
    }

    public Item findById(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));
    }

    public Item create(Item item, int userId, int requestId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        item.setOwner(owner);

        if (requestId != 0) {
            Request request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Запрос не найден."));

            item.setRequest(request);
        }

        itemRepository.save(item);
        return item;
    }

    public Item update(int itemId, Item patchItem, int userId) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));

        if (oldItem.getOwner().getId() != userId) {
            throw new NotFoundException("Товар не найден.");
        }


        patchItem.setId(itemId);
        patchItem.setOwner(oldItem.getOwner());

        if (patchItem.getName() == null) {
            patchItem.setName(oldItem.getName());
        }

        if (patchItem.getDescription() == null) {
            patchItem.setDescription(oldItem.getDescription());
        }

        if (patchItem.getAvailable() == null) {
            patchItem.setAvailable(oldItem.getAvailable());
        }

        itemRepository.save(patchItem);
        return patchItem;
    }

    public Page<Item> findByText(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return Page.empty();
        }

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        return itemRepository.findAllByText(text, pageable);
    }

    public Comment addComment(Comment comment, int itemId, int authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));

        //Нельзя оставлять комментарий к товару которым не пользовались
        //проверка по userId и itemId, статус и дата старта в прошлом (т.е комментатор уже попользовался товаром)
        Booking booking = bookingRepository.findAllByBooker_IdAndItem_IdAndStatusAndStartDateBeforeOrderByStartDateAsc(
                authorId, itemId, BookingStatus.APPROVED, LocalDateTime.now()).stream()
                .findFirst()
                .orElseThrow(() -> new ValidationException("Вы не можете оставлять отзыв на этот товар."));


        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        commentRepository.save(comment);
        return comment;
    }
}
