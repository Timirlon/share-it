package org.example.shareit.bookings;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.shareit.bookings.dtos.BookingCreateDto;
import org.example.shareit.bookings.dtos.BookingMapper;
import org.example.shareit.items.Item;
import org.example.shareit.items.comments.dtos.CommentMapper;
import org.example.shareit.items.dtos.ItemMapper;
import org.example.shareit.users.User;
import org.example.shareit.users.dtos.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.example.shareit.utils.RequestConstants.USER_ID_REQUEST_HEADER;

import java.time.LocalDateTime;

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
    void createTestSuccess() {
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


}
