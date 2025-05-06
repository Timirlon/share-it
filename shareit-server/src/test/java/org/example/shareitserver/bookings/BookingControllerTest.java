package org.example.shareitserver.bookings;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.shareitserver.bookings.dtos.BookingCreateDto;
import org.example.shareitserver.bookings.dtos.BookingMapper;
import org.example.shareitserver.items.Item;
import org.example.shareitserver.items.comments.dtos.CommentMapper;
import org.example.shareitserver.items.dtos.ItemMapper;
import org.example.shareitserver.users.User;
import org.example.shareitserver.users.dtos.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.example.shareitserver.utils.RequestConstants.USER_ID_REQUEST_HEADER;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest({BookingController.class, BookingMapper.class,
        ItemMapper.class, UserMapper.class, CommentMapper.class})
public class BookingControllerTest {
    static final String BASE_URL = "/bookings";

    @MockitoBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void createTest() {
        int bookerId = 1;
        String bookerName = "booker-test";
        String bookerEmail = "booker-test@mail.com";
        User booker = new User();
        booker.setId(bookerId);
        booker.setName(bookerName);
        booker.setEmail(bookerEmail);

        int bookedItemId = 1;
        String bookedItemName = "booked-item-name-test";
        String bookedItemDesc = "booked-item-desc-test";
        Item bookedItem = new Item();
        bookedItem.setId(bookedItemId);
        bookedItem.setName(bookedItemName);
        bookedItem.setDescription(bookedItemDesc);
        bookedItem.setAvailable(true);
        bookedItem.setOwner(booker);

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = start.plusHours(5);
        int bookingId = 1;

        BookingCreateDto booking = new BookingCreateDto();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItemId(bookingId);


        Mockito.when(bookingService.create(Mockito.any(Booking.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(
                        invocationOnMock -> {
                            Booking returnValue = new Booking();
                            returnValue.setId(bookingId);
                            returnValue.setStartDate(start);
                            returnValue.setEndDate(end);
                            returnValue.setItem(bookedItem);
                            returnValue.setBooker(booker);
                            returnValue.setStatus(BookingStatus.WAITING);

                            return returnValue;
                        });


        String bookingInJson = objectMapper.writeValueAsString(booking);

        mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingInJson)
                        .header(USER_ID_REQUEST_HEADER, bookerId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item.id").value(bookedItemId))
                .andExpect(jsonPath("$.booker.id").value(bookerId))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.toString()));
    }

    @Test
    @SneakyThrows
    void updateStatusTest() {
        boolean isApproved = true;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item item = new Item();
        int itemId = 1;
        item.setId(itemId);
        User itemOwner = new User();
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);


        Mockito.when(bookingService.updateStatus(bookingId, bookerId, isApproved))
                .thenReturn(booking);


        mockMvc.perform(patch("/bookings/" + bookingId
                + "?approved=" + isApproved)
                    .header(USER_ID_REQUEST_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item.id").value(itemId))
                .andExpect(jsonPath("$.booker.id").value(bookerId))
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.toString()));
    }

    @Test
    @SneakyThrows
    void findByIdTest() {
        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item item = new Item();
        int itemId = 1;
        item.setId(itemId);
        User itemOwner = new User();
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);


        Mockito.when(bookingService.findById(bookingId, bookerId))
                .thenReturn(booking);


        mockMvc.perform(get("/bookings/" + bookingId)
                        .header(USER_ID_REQUEST_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item.id").value(itemId))
                .andExpect(jsonPath("$.booker.id").value(bookerId))
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.toString()));
    }

    @Test
    @SneakyThrows
    void findMyBookingTest() {
        int expectedSize = 1;

        User booker = new User();
        int bookerId = 1;
        booker.setId(bookerId);
        booker.setName("test-booker-name");

        Item item = new Item();
        int itemId = 1;
        item.setId(itemId);
        User itemOwner = new User();
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);

        BookingFilteringState defaultState = BookingFilteringState.ALL;
        int defaultPage = 0;
        int defaultSize = 10;

        Mockito.when(bookingService.findAllByBookerId(
                bookerId, defaultState, defaultPage, defaultSize))
                .thenReturn(new PageImpl<>(
                        List.of(booking)));


        mockMvc.perform(get("/bookings")
                        .header(USER_ID_REQUEST_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expectedSize))
                .andExpect(jsonPath("$.[0].id").value(bookingId))
                .andExpect(jsonPath("$.[0].start").exists())
                .andExpect(jsonPath("$.[0].end").exists())
                .andExpect(jsonPath("$.[0].item.id").value(itemId))
                .andExpect(jsonPath("$.[0].booker.id").value(bookerId))
                .andExpect(jsonPath("$.[0].status").value(BookingStatus.APPROVED.toString()));
    }

    @Test
    @SneakyThrows
    void findBookingOfMyItemTest() {
        int expectedSize = 1;

        User user = new User();
        int bookerId = 1;
        user.setId(bookerId);
        user.setName("test-user-name-1");

        User itemOwner = new User();
        int ownerId = 2;
        itemOwner.setId(ownerId);
        itemOwner.setName("test-user-name-2");

        Item item = new Item();
        int itemId = 1;
        item.setId(itemId);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);

        BookingFilteringState defaultState = BookingFilteringState.ALL;
        int defaultPage = 0;
        int defaultSize = 10;

        Mockito.when(bookingService.findAllByItemOwnerId(
                        ownerId, defaultState, defaultPage, defaultSize))
                .thenReturn(new PageImpl<>(
                        List.of(booking)));


        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_REQUEST_HEADER, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expectedSize))
                .andExpect(jsonPath("$.[0].id").value(bookingId))
                .andExpect(jsonPath("$.[0].start").exists())
                .andExpect(jsonPath("$.[0].end").exists())
                .andExpect(jsonPath("$.[0].item.id").value(itemId))
                .andExpect(jsonPath("$.[0].booker.id").value(bookerId))
                .andExpect(jsonPath("$.[0].status").value(BookingStatus.APPROVED.toString()));
    }

    // Test filtering states
    @Test
    @SneakyThrows
    void findBookingOfMyItemTestWithStateCurrent() {
        int expectedSize = 1;

        User user = new User();
        int bookerId = 1;
        user.setId(bookerId);
        user.setName("test-user-name-1");

        User itemOwner = new User();
        int ownerId = 2;
        itemOwner.setId(ownerId);
        itemOwner.setName("test-user-name-2");

        Item item = new Item();
        int itemId = 1;
        item.setId(itemId);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);

        BookingFilteringState givenState = BookingFilteringState.CURRENT;
        int defaultPage = 0;
        int defaultSize = 10;

        Mockito.when(bookingService.findAllByItemOwnerId(
                        ownerId, givenState, defaultPage, defaultSize))
                .thenReturn(new PageImpl<>(
                        List.of(booking)));


        mockMvc.perform(get("/bookings/owner")
                        .param("state", "CURRENT")
                        .header(USER_ID_REQUEST_HEADER, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expectedSize))
                .andExpect(jsonPath("$.[0].id").value(bookingId))
                .andExpect(jsonPath("$.[0].start").exists())
                .andExpect(jsonPath("$.[0].end").exists())
                .andExpect(jsonPath("$.[0].item.id").value(itemId))
                .andExpect(jsonPath("$.[0].booker.id").value(bookerId))
                .andExpect(jsonPath("$.[0].status").value(BookingStatus.APPROVED.toString()));
    }

    @Test
    @SneakyThrows
    void findBookingOfMyItemTestWithStatePast() {
        int expectedSize = 1;

        User user = new User();
        int bookerId = 1;
        user.setId(bookerId);
        user.setName("test-user-name-1");

        User itemOwner = new User();
        int ownerId = 2;
        itemOwner.setId(ownerId);
        itemOwner.setName("test-user-name-2");

        Item item = new Item();
        int itemId = 1;
        item.setId(itemId);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now().minusHours(5));
        booking.setEndDate(LocalDateTime.now().minusHours(2));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);

        BookingFilteringState givenState = BookingFilteringState.PAST;
        int defaultPage = 0;
        int defaultSize = 10;

        Mockito.when(bookingService.findAllByItemOwnerId(
                        ownerId, givenState, defaultPage, defaultSize))
                .thenReturn(new PageImpl<>(
                        List.of(booking)));


        mockMvc.perform(get("/bookings/owner")
                        .param("state", "PAST")
                        .header(USER_ID_REQUEST_HEADER, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expectedSize))
                .andExpect(jsonPath("$.[0].id").value(bookingId))
                .andExpect(jsonPath("$.[0].start").exists())
                .andExpect(jsonPath("$.[0].end").exists())
                .andExpect(jsonPath("$.[0].item.id").value(itemId))
                .andExpect(jsonPath("$.[0].booker.id").value(bookerId))
                .andExpect(jsonPath("$.[0].status").value(BookingStatus.APPROVED.toString()));
    }

    @Test
    @SneakyThrows
    void findBookingOfMyItemTestWithStateFuture() {
        int expectedSize = 1;

        User user = new User();
        int bookerId = 1;
        user.setId(bookerId);
        user.setName("test-user-name-1");

        User itemOwner = new User();
        int ownerId = 2;
        itemOwner.setId(ownerId);
        itemOwner.setName("test-user-name-2");

        Item item = new Item();
        int itemId = 1;
        item.setId(itemId);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now().plusHours(2));
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);

        BookingFilteringState givenState = BookingFilteringState.FUTURE;
        int defaultPage = 0;
        int defaultSize = 10;

        Mockito.when(bookingService.findAllByItemOwnerId(
                        ownerId, givenState, defaultPage, defaultSize))
                .thenReturn(new PageImpl<>(
                        List.of(booking)));


        mockMvc.perform(get("/bookings/owner")
                        .param("state", "FUTURE")
                        .header(USER_ID_REQUEST_HEADER, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expectedSize))
                .andExpect(jsonPath("$.[0].id").value(bookingId))
                .andExpect(jsonPath("$.[0].start").exists())
                .andExpect(jsonPath("$.[0].end").exists())
                .andExpect(jsonPath("$.[0].item.id").value(itemId))
                .andExpect(jsonPath("$.[0].booker.id").value(bookerId))
                .andExpect(jsonPath("$.[0].status").value(BookingStatus.APPROVED.toString()));
    }

    @Test
    @SneakyThrows
    void findBookingOfMyItemTestWithStateRejected() {
        int expectedSize = 1;

        User user = new User();
        int bookerId = 1;
        user.setId(bookerId);
        user.setName("test-user-name-1");

        User itemOwner = new User();
        int ownerId = 2;
        itemOwner.setId(ownerId);
        itemOwner.setName("test-user-name-2");

        Item item = new Item();
        int itemId = 1;
        item.setId(itemId);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.REJECTED);
        booking.setBooker(user);
        booking.setItem(item);

        BookingFilteringState givenState = BookingFilteringState.REJECTED;
        int defaultPage = 0;
        int defaultSize = 10;

        Mockito.when(bookingService.findAllByItemOwnerId(
                        ownerId, givenState, defaultPage, defaultSize))
                .thenReturn(new PageImpl<>(
                        List.of(booking)));


        mockMvc.perform(get("/bookings/owner")
                        .param("state", "REJECTED")
                        .header(USER_ID_REQUEST_HEADER, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expectedSize))
                .andExpect(jsonPath("$.[0].id").value(bookingId))
                .andExpect(jsonPath("$.[0].start").exists())
                .andExpect(jsonPath("$.[0].end").exists())
                .andExpect(jsonPath("$.[0].item.id").value(itemId))
                .andExpect(jsonPath("$.[0].booker.id").value(bookerId))
                .andExpect(jsonPath("$.[0].status").value(BookingStatus.REJECTED.toString()));
    }

    @Test
    @SneakyThrows
    void findBookingOfMyItemTestWithStateWaiting() {
        int expectedSize = 1;

        User user = new User();
        int bookerId = 1;
        user.setId(bookerId);
        user.setName("test-user-name-1");

        User itemOwner = new User();
        int ownerId = 2;
        itemOwner.setId(ownerId);
        itemOwner.setName("test-user-name-2");

        Item item = new Item();
        int itemId = 1;
        item.setId(itemId);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        int bookingId = 1;
        booking.setId(bookingId);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);

        BookingFilteringState givenState = BookingFilteringState.WAITING;
        int defaultPage = 0;
        int defaultSize = 10;

        Mockito.when(bookingService.findAllByItemOwnerId(
                        ownerId, givenState, defaultPage, defaultSize))
                .thenReturn(new PageImpl<>(
                        List.of(booking)));


        mockMvc.perform(get("/bookings/owner")
                        .param("state", "WAITING")
                        .header(USER_ID_REQUEST_HEADER, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expectedSize))
                .andExpect(jsonPath("$.[0].id").value(bookingId))
                .andExpect(jsonPath("$.[0].start").exists())
                .andExpect(jsonPath("$.[0].end").exists())
                .andExpect(jsonPath("$.[0].item.id").value(itemId))
                .andExpect(jsonPath("$.[0].booker.id").value(bookerId))
                .andExpect(jsonPath("$.[0].status").value(BookingStatus.WAITING.toString()));
    }

    @Test
    @SneakyThrows
    void findBookingOfMyItemTestWithIncorrectState() {
        String expectedMessage = "Некорректное состояние для фильтрации записей";

        int ownerId = 1;

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "INCORRECT_STATE")
                        .header(USER_ID_REQUEST_HEADER, ownerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.desc").value(expectedMessage));
    }
}
