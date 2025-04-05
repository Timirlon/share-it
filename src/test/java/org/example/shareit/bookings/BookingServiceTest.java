package org.example.shareit.bookings;

import jakarta.validation.ValidationException;
import lombok.SneakyThrows;
import org.example.shareit.exceptions.ForbiddenAccessException;
import org.example.shareit.exceptions.NotFoundException;
import org.example.shareit.items.Item;
import org.example.shareit.items.ItemRepository;
import org.example.shareit.users.User;
import org.example.shareit.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    BookingService bookingService;


    @Test
    void createTestBookerNotFoundFail() {
        String expectedMessage = "Пользователь не найден.";

        Booking booking = new Booking();
        int bookerId = 1;
        int bookedItemId = 1;

        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.empty());


        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.create(booking, bookerId, bookedItemId)
        );

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void createTestItemNotFoundFail() {
        String expectedMessage = "Товар не найден.";

        Booking booking = new Booking();

        int bookedItemId = 1;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);

        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(itemRepository.findById(bookedItemId))
                .thenReturn(Optional.empty());


        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.create(booking, bookerId, bookedItemId)
        );

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void createTestItemNotAvailable() {
        String expectedMessage = "Товар недоступен.";

        Booking booking = new Booking();

        Item bookedItem = new Item();
        int bookedItemId = 1;
        bookedItem.setId(bookedItemId);
        bookedItem.setAvailable(false);

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);

        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(itemRepository.findById(bookedItemId))
                .thenReturn(Optional.of(bookedItem));


        ValidationException e = assertThrows(
                ValidationException.class,
                () -> bookingService.create(booking, bookerId, bookedItemId)
        );

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void createTestSuccess() {
        BookingStatus expectedStatus = BookingStatus.WAITING;

        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(10));

        Item bookedItem = new Item();
        int bookedItemId = 1;
        bookedItem.setId(bookedItemId);
        bookedItem.setAvailable(true);

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);

        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(itemRepository.findById(bookedItemId))
                .thenReturn(Optional.of(bookedItem));


        Booking created = bookingService.create(
                booking, bookerId, bookedItemId);


        assertEquals(booking.getStartDate(), created.getStartDate());
        assertEquals(booking.getEndDate(), created.getEndDate());
        assertEquals(expectedStatus, created.getStatus());
        assertEquals(bookerId, created.getBooker().getId());
        assertEquals(bookedItemId, created.getItem().getId());
    }

    @Test
    @SneakyThrows
    void updateStatusTestBookingNotFound() {
        String expectedMessage = "Запись не найдена.";

        int bookingId = 1;
        int userId = 1;
        boolean isApproved = true;

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());


        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatus(bookingId, userId, isApproved));

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void updateStatusTestApprovedByWrongUser() {
        String expectedMessage = "Вы не можете одобрить запрос.";

        User owner = new User();
        int ownerId = 1;
        owner.setId(ownerId);

        Item bookedItem = new Item();
        bookedItem.setOwner(owner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(10));
        booking.setItem(bookedItem);


        int wrongUserId = 999;
        boolean isApproved = true;

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));


        ForbiddenAccessException e = assertThrows(
                ForbiddenAccessException.class,
                () -> bookingService.updateStatus(bookingId, wrongUserId, isApproved)
        );

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void updateStatusTestApproveBooking() {
        User owner = new User();
        int ownerId = 1;
        owner.setId(ownerId);

        User booker = new User();
        int bookerId = 2;
        booker.setId(bookerId);

        Item bookedItem = new Item();
        int bookedItemId = 5;
        bookedItem.setId(bookedItemId);
        bookedItem.setOwner(owner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(10));
        booking.setItem(bookedItem);
        booking.setBooker(booker);

        boolean isApproved = true;


        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));


        Booking updated = bookingService.updateStatus(
                bookingId, ownerId, isApproved
        );


        assertEquals(bookingId, updated.getId());
        assertEquals(booking.getStartDate(), updated.getStartDate());
        assertEquals(booking.getEndDate(), updated.getEndDate());
        assertEquals(bookedItemId, updated.getItem().getId());
        assertEquals(bookerId, updated.getBooker().getId());
        assertEquals(BookingStatus.APPROVED, updated.getStatus());
    }

    @Test
    @SneakyThrows
    void updateStatusTestRejectBooking() {
        User owner = new User();
        int ownerId = 1;
        owner.setId(ownerId);

        User booker = new User();
        int bookerId = 2;
        booker.setId(bookerId);

        Item bookedItem = new Item();
        int bookedItemId = 5;
        bookedItem.setId(bookedItemId);
        bookedItem.setOwner(owner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(10));
        booking.setItem(bookedItem);
        booking.setBooker(booker);

        boolean isApproved = false;


        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));


        Booking updated = bookingService.updateStatus(
                bookingId, ownerId, isApproved
        );


        assertEquals(bookingId, updated.getId());
        assertEquals(booking.getStartDate(), updated.getStartDate());
        assertEquals(booking.getEndDate(), updated.getEndDate());
        assertEquals(bookedItemId, updated.getItem().getId());
        assertEquals(bookerId, updated.getBooker().getId());
        assertEquals(BookingStatus.REJECTED, updated.getStatus());
    }

    @Test
    @SneakyThrows
    void findByIdTestBookingNonExistent() {
        String expectedMessage = "Запись не найдена.";

        int bookingId = 1;
        int userId = 1;

        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());


        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.findById(bookingId, userId)
        );

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void findByIdTestWrongUserAccess() {
        String expectedMessage = "Запись не найдена.";

        int wrongUserId = 999;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);

        User owner = new User();
        int ownerId = 2;
        owner.setId(ownerId);

        Item bookedItem = new Item();
        bookedItem.setOwner(owner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));


        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.findById(bookingId, wrongUserId)
        );

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void findByIdTestBookerAccess() {
        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);

        User owner = new User();
        int ownerId = 2;
        owner.setId(ownerId);

        Item bookedItem = new Item();
        bookedItem.setOwner(owner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(10));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));


        Booking found = bookingService.findById(bookingId, bookerId);


        assertEquals(bookingId, found.getId());
        assertEquals(bookerId, found.getBooker().getId());
        assertEquals(ownerId, found.getItem().getOwner().getId());
    }

    @Test
    @SneakyThrows
    void findByIdTestBookedItemOwnerAccess() {
        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);

        User owner = new User();
        int ownerId = 2;
        owner.setId(ownerId);

        Item bookedItem = new Item();
        bookedItem.setOwner(owner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(10));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));


        Booking found = bookingService.findById(bookingId, ownerId);


        assertEquals(bookingId, found.getId());
        assertEquals(bookerId, found.getBooker().getId());
        assertEquals(ownerId, found.getItem().getOwner().getId());
    }

    @Test
    @SneakyThrows
    void findAllByBookerIdTestBookerNotFound() {
        String expectedMessage = "Пользователь не найден.";

        int bookerId = 1;
        BookingFilteringState state = BookingFilteringState.ALL;
        int page = 0;
        int size = 10;

        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.empty());


        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.findAllByBookerId(
                        bookerId, state, page, size)
        );

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void findAllByBookerIdTestFilteringStateAll() {
        int expectedSize = 1;

        BookingFilteringState state = BookingFilteringState.ALL;
        int page = 0;
        int size = 10;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item bookedItem = new Item();
        int itemId = 1;
        bookedItem.setId(itemId);
        User itemOwner = new User();
        bookedItem.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByBooker_IdOrderByStartDateDesc(
                bookerId, PageRequest.of(page, size)))
                .thenReturn(List.of(booking));


        List<Booking> found = bookingService.findAllByBookerId(
                bookerId, state, page, size);


        assertEquals(expectedSize, found.size());
        assertEquals(bookingId, found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(bookerId, found.get(0).getBooker().getId());
        assertEquals(itemId, found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByBookerIdTestFilteringStateCurrent() {
        int expectedSize = 1;

        BookingFilteringState state = BookingFilteringState.CURRENT;
        int page = 0;
        int size = 10;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item bookedItem = new Item();
        int itemId = 1;
        bookedItem.setId(itemId);
        User itemOwner = new User();
        bookedItem.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByBooker_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(
                        Mockito.anyInt(), Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));


        List<Booking> found = bookingService.findAllByBookerId(
                bookerId, state, page, size);


        assertEquals(expectedSize, found.size());
        assertEquals(bookingId, found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(bookerId, found.get(0).getBooker().getId());
        assertEquals(itemId, found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByBookerIdTestFilteringStatePast() {
        int expectedSize = 1;

        BookingFilteringState state = BookingFilteringState.PAST;
        int page = 0;
        int size = 10;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item bookedItem = new Item();
        int itemId = 1;
        bookedItem.setId(itemId);
        User itemOwner = new User();
        bookedItem.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByBooker_IdAndEndDateIsBeforeOrderByStartDateDesc(
                        Mockito.anyInt(), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));


        List<Booking> found = bookingService.findAllByBookerId(
                bookerId, state, page, size);


        assertEquals(expectedSize, found.size());
        assertEquals(bookingId, found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(bookerId, found.get(0).getBooker().getId());
        assertEquals(itemId, found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByBookerIdTestFilteringStateFuture() {
        int expectedSize = 1;

        BookingFilteringState state = BookingFilteringState.FUTURE;
        int page = 0;
        int size = 10;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item bookedItem = new Item();
        int itemId = 1;
        bookedItem.setId(itemId);
        User itemOwner = new User();
        bookedItem.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByBooker_IdAndStartDateIsAfterOrderByStartDateDesc(
                        Mockito.anyInt(), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));


        List<Booking> found = bookingService.findAllByBookerId(
                bookerId, state, page, size);


        assertEquals(expectedSize, found.size());
        assertEquals(bookingId, found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(bookerId, found.get(0).getBooker().getId());
        assertEquals(itemId, found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByBookerIdTestFilteringStateWaiting() {
        int expectedSize = 1;
        BookingStatus status = BookingStatus.WAITING;

        BookingFilteringState state = BookingFilteringState.WAITING;
        int page = 0;
        int size = 10;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item bookedItem = new Item();
        int itemId = 1;
        bookedItem.setId(itemId);
        User itemOwner = new User();
        bookedItem.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(
                bookerId, status, PageRequest.of(page, size)
                ))
                .thenReturn(List.of(booking));


        List<Booking> found = bookingService.findAllByBookerId(
                bookerId, state, page, size);


        assertEquals(expectedSize, found.size());
        assertEquals(bookingId, found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(bookerId, found.get(0).getBooker().getId());
        assertEquals(itemId, found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByBookerIdTestFilteringStateRejected() {
        int expectedSize = 1;
        BookingStatus status = BookingStatus.REJECTED;

        BookingFilteringState state = BookingFilteringState.REJECTED;
        int page = 0;
        int size = 10;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item bookedItem = new Item();
        int itemId = 1;
        bookedItem.setId(itemId);
        User itemOwner = new User();
        bookedItem.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(
                        bookerId, status, PageRequest.of(page, size)
                ))
                .thenReturn(List.of(booking));


        List<Booking> found = bookingService.findAllByBookerId(
                bookerId, state, page, size);


        assertEquals(expectedSize, found.size());
        assertEquals(bookingId, found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(bookerId, found.get(0).getBooker().getId());
        assertEquals(itemId, found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByOwnerIdTestOwnerNotFound() {
        String expectedMessage = "Пользователь не найден.";

        int ownerId = 1;
        BookingFilteringState state = BookingFilteringState.ALL;
        int page = 0;
        int size = 10;

        Mockito.when(userRepository.findById(ownerId))
                .thenReturn(Optional.empty());


        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.findAllByItemOwnerId(
                        ownerId, state, page, size)
        );

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    @SneakyThrows
    void findAllByItemOwnerIdTestFilteringStateAll() {
        int expectedSize = 1;

        BookingFilteringState state = BookingFilteringState.ALL;
        int page = 0;
        int size = 10;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item bookedItem = new Item();
        int itemId = 1;
        bookedItem.setId(itemId);
        User itemOwner = new User();
        bookedItem.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByItem_Owner_IdOrderByStartDateDesc(
                        bookerId, PageRequest.of(page, size)))
                .thenReturn(List.of(booking));


        List<Booking> found = bookingService.findAllByItemOwnerId(
                bookerId, state, page, size);


        assertEquals(expectedSize, found.size());
        assertEquals(bookingId, found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(bookerId, found.get(0).getBooker().getId());
        assertEquals(itemId, found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByItemOwnerIdTestFilteringStateCurrent() {
        int expectedSize = 1;

        BookingFilteringState state = BookingFilteringState.CURRENT;
        int page = 0;
        int size = 10;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item bookedItem = new Item();
        int itemId = 1;
        bookedItem.setId(itemId);
        User itemOwner = new User();
        bookedItem.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByItem_Owner_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(
                        Mockito.anyInt(), Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));


        List<Booking> found = bookingService.findAllByItemOwnerId(
                bookerId, state, page, size);


        assertEquals(expectedSize, found.size());
        assertEquals(bookingId, found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(bookerId, found.get(0).getBooker().getId());
        assertEquals(itemId, found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByItemOwnerIdTestFilteringStatePast() {
        int expectedSize = 1;

        BookingFilteringState state = BookingFilteringState.PAST;
        int page = 0;
        int size = 10;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item bookedItem = new Item();
        int itemId = 1;
        bookedItem.setId(itemId);
        User itemOwner = new User();
        bookedItem.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByItem_Owner_IdAndEndDateIsBeforeOrderByStartDateDesc(
                        Mockito.anyInt(), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));


        List<Booking> found = bookingService.findAllByItemOwnerId(
                bookerId, state, page, size);


        assertEquals(expectedSize, found.size());
        assertEquals(bookingId, found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(bookerId, found.get(0).getBooker().getId());
        assertEquals(itemId, found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByItemOwnerIdTestFilteringStateFuture() {
        int expectedSize = 1;

        BookingFilteringState state = BookingFilteringState.FUTURE;
        int page = 0;
        int size = 10;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item bookedItem = new Item();
        int itemId = 1;
        bookedItem.setId(itemId);
        User itemOwner = new User();
        bookedItem.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByItem_Owner_IdAndStartDateIsAfterOrderByStartDateDesc(
                        Mockito.anyInt(), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));


        List<Booking> found = bookingService.findAllByItemOwnerId(
                bookerId, state, page, size);


        assertEquals(expectedSize, found.size());
        assertEquals(bookingId, found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(bookerId, found.get(0).getBooker().getId());
        assertEquals(itemId, found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByItemOwnerIdTestFilteringStateWaiting() {
        int expectedSize = 1;
        BookingStatus status = BookingStatus.WAITING;

        BookingFilteringState state = BookingFilteringState.WAITING;
        int page = 0;
        int size = 10;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item bookedItem = new Item();
        int itemId = 1;
        bookedItem.setId(itemId);
        User itemOwner = new User();
        bookedItem.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(
                        bookerId, status, PageRequest.of(page, size)
                ))
                .thenReturn(List.of(booking));


        List<Booking> found = bookingService.findAllByItemOwnerId(
                bookerId, state, page, size);


        assertEquals(expectedSize, found.size());
        assertEquals(bookingId, found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(bookerId, found.get(0).getBooker().getId());
        assertEquals(itemId, found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByItemOwnerIdTestFilteringStateRejected() {
        int expectedSize = 1;
        BookingStatus status = BookingStatus.REJECTED;

        BookingFilteringState state = BookingFilteringState.REJECTED;
        int page = 0;
        int size = 10;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item bookedItem = new Item();
        int itemId = 1;
        bookedItem.setId(itemId);
        User itemOwner = new User();
        bookedItem.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);


        Mockito.when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(
                        bookerId, status, PageRequest.of(page, size)
                ))
                .thenReturn(List.of(booking));


        List<Booking> found = bookingService.findAllByItemOwnerId(
                bookerId, state, page, size);


        assertEquals(expectedSize, found.size());
        assertEquals(bookingId, found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(bookerId, found.get(0).getBooker().getId());
        assertEquals(itemId, found.get(0).getItem().getId());
    }
}
