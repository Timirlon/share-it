package org.example.shareit.bookings;

import lombok.SneakyThrows;
import org.example.shareit.items.Item;
import org.example.shareit.items.ItemRepository;
import org.example.shareit.users.User;
import org.example.shareit.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @SneakyThrows
    void findAllByBooker_IdOrderByStartDateDescTest() {
        int expectedSize = 1;

        int page = 0;
        int size = 10;

        User booker = new User();
        booker.setName("test-booker-name-1");
        booker.setEmail("test-booker-email-1");
        userRepository.save(booker);

        User itemOwner = new User();
        booker.setName("test-booker-name-2");
        booker.setEmail("test-booker-email-2");
        userRepository.save(itemOwner);

        Item bookedItem = new Item();
        bookedItem.setName("test-item-name-1");
        bookedItem.setDescription("test-item-desc-1");
        boolean isAvailable = true;
        bookedItem.setAvailable(isAvailable);
        bookedItem.setOwner(itemOwner);
        itemRepository.save(bookedItem);

        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);
        bookingRepository.save(booking);


        List<Booking> found = bookingRepository.findAllByBooker_IdOrderByStartDateDesc(
                booker.getId(), PageRequest.of(page, size));

        assertEquals(expectedSize, found.size());
        assertEquals(booking.getId(), found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(booking.getBooker().getId(), found.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByBooker_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDescTest() {
        int expectedSize = 1;

        int page = 0;
        int size = 10;

        User booker = new User();
        booker.setName("test-booker-name-1");
        booker.setEmail("test-booker-email-1");
        userRepository.save(booker);

        User itemOwner = new User();
        booker.setName("test-booker-name-2");
        booker.setEmail("test-booker-email-2");
        userRepository.save(itemOwner);

        Item bookedItem = new Item();
        bookedItem.setName("test-item-name-1");
        bookedItem.setDescription("test-item-desc-1");
        boolean isAvailable = true;
        bookedItem.setAvailable(isAvailable);
        bookedItem.setOwner(itemOwner);
        itemRepository.save(bookedItem);

        Booking firstBooking = new Booking();
        firstBooking.setStartDate(LocalDateTime.now().minusHours(1));
        firstBooking.setEndDate(LocalDateTime.now().plusHours(5));
        firstBooking.setStatus(BookingStatus.APPROVED);
        firstBooking.setBooker(booker);
        firstBooking.setItem(bookedItem);
        bookingRepository.save(firstBooking);

        Booking secondBooking = new Booking();
        secondBooking.setStartDate(LocalDateTime.now().minusHours(10));
        secondBooking.setEndDate(LocalDateTime.now().minusHours(5));
        secondBooking.setStatus(BookingStatus.APPROVED);
        secondBooking.setBooker(booker);
        secondBooking.setItem(bookedItem);
        bookingRepository.save(secondBooking);


        List<Booking> found = bookingRepository.findAllByBooker_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(
                booker.getId(), LocalDateTime.now(),
                LocalDateTime.now(), PageRequest.of(page, size));


        assertEquals(expectedSize, found.size());
        assertEquals(firstBooking.getId(), found.get(0).getId());
        assertEquals(firstBooking.getStartDate(), found.get(0).getStartDate());
        assertEquals(firstBooking.getEndDate(), found.get(0).getEndDate());
        assertEquals(firstBooking.getStatus(), found.get(0).getStatus());
        assertEquals(firstBooking.getBooker().getId(), found.get(0).getBooker().getId());
        assertEquals(firstBooking.getItem().getId(), found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByBooker_IdAndEndDateIsBeforeOrderByStartDateDescTest() {
        int expectedSize = 1;

        int page = 0;
        int size = 10;

        User booker = new User();
        booker.setName("test-booker-name-1");
        booker.setEmail("test-booker-email-1");
        userRepository.save(booker);

        User itemOwner = new User();
        booker.setName("test-booker-name-2");
        booker.setEmail("test-booker-email-2");
        userRepository.save(itemOwner);

        Item bookedItem = new Item();
        bookedItem.setName("test-item-name-1");
        bookedItem.setDescription("test-item-desc-1");
        boolean isAvailable = true;
        bookedItem.setAvailable(isAvailable);
        bookedItem.setOwner(itemOwner);
        itemRepository.save(bookedItem);

        Booking firstBooking = new Booking();
        firstBooking.setStartDate(LocalDateTime.now().minusHours(10));
        firstBooking.setEndDate(LocalDateTime.now().plusHours(5));
        firstBooking.setStatus(BookingStatus.APPROVED);
        firstBooking.setBooker(booker);
        firstBooking.setItem(bookedItem);
        bookingRepository.save(firstBooking);

        Booking secondBooking = new Booking();
        secondBooking.setStartDate(LocalDateTime.now().minusHours(10));
        secondBooking.setEndDate(LocalDateTime.now().minusHours(5));
        secondBooking.setStatus(BookingStatus.APPROVED);
        secondBooking.setBooker(booker);
        secondBooking.setItem(bookedItem);
        bookingRepository.save(secondBooking);


        List<Booking> found = bookingRepository.findAllByBooker_IdAndEndDateIsBeforeOrderByStartDateDesc(
                booker.getId(), LocalDateTime.now(), PageRequest.of(page, size));


        assertEquals(expectedSize, found.size());
        assertEquals(secondBooking.getId(), found.get(0).getId());
        assertEquals(secondBooking.getStartDate(), found.get(0).getStartDate());
        assertEquals(secondBooking.getEndDate(), found.get(0).getEndDate());
        assertEquals(secondBooking.getStatus(), found.get(0).getStatus());
        assertEquals(secondBooking.getBooker().getId(), found.get(0).getBooker().getId());
        assertEquals(secondBooking.getItem().getId(), found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByBooker_IdAndStartDateIsAfterOrderByStartDateDescTest() {
        int expectedSize = 1;

        int page = 0;
        int size = 10;

        User booker = new User();
        booker.setName("test-booker-name-1");
        booker.setEmail("test-booker-email-1");
        userRepository.save(booker);

        User itemOwner = new User();
        booker.setName("test-booker-name-2");
        booker.setEmail("test-booker-email-2");
        userRepository.save(itemOwner);

        Item bookedItem = new Item();
        bookedItem.setName("test-item-name-1");
        bookedItem.setDescription("test-item-desc-1");
        boolean isAvailable = true;
        bookedItem.setAvailable(isAvailable);
        bookedItem.setOwner(itemOwner);
        itemRepository.save(bookedItem);

        Booking firstBooking = new Booking();
        firstBooking.setStartDate(LocalDateTime.now().plusHours(1));
        firstBooking.setEndDate(LocalDateTime.now().plusHours(5));
        firstBooking.setStatus(BookingStatus.APPROVED);
        firstBooking.setBooker(booker);
        firstBooking.setItem(bookedItem);
        bookingRepository.save(firstBooking);

        Booking secondBooking = new Booking();
        secondBooking.setStartDate(LocalDateTime.now().minusHours(10));
        secondBooking.setEndDate(LocalDateTime.now().minusHours(5));
        secondBooking.setStatus(BookingStatus.APPROVED);
        secondBooking.setBooker(booker);
        secondBooking.setItem(bookedItem);
        bookingRepository.save(secondBooking);


        List<Booking> found = bookingRepository.findAllByBooker_IdAndStartDateIsAfterOrderByStartDateDesc(
                booker.getId(), LocalDateTime.now(), PageRequest.of(page, size));


        assertEquals(expectedSize, found.size());
        assertEquals(firstBooking.getId(), found.get(0).getId());
        assertEquals(firstBooking.getStartDate(), found.get(0).getStartDate());
        assertEquals(firstBooking.getEndDate(), found.get(0).getEndDate());
        assertEquals(firstBooking.getStatus(), found.get(0).getStatus());
        assertEquals(firstBooking.getBooker().getId(), found.get(0).getBooker().getId());
        assertEquals(firstBooking.getItem().getId(), found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByBooker_IdAndStatusOrderByStartDateDescTest() {
        int expectedSize = 1;

        int page = 0;
        int size = 10;

        User booker = new User();
        booker.setName("test-booker-name-1");
        booker.setEmail("test-booker-email-1");
        userRepository.save(booker);

        User itemOwner = new User();
        booker.setName("test-booker-name-2");
        booker.setEmail("test-booker-email-2");
        userRepository.save(itemOwner);

        Item bookedItem = new Item();
        bookedItem.setName("test-item-name-1");
        bookedItem.setDescription("test-item-desc-1");
        boolean isAvailable = true;
        bookedItem.setAvailable(isAvailable);
        bookedItem.setOwner(itemOwner);
        itemRepository.save(bookedItem);

        Booking firstBooking = new Booking();
        firstBooking.setStartDate(LocalDateTime.now().plusHours(1));
        firstBooking.setEndDate(LocalDateTime.now().plusHours(5));
        firstBooking.setStatus(BookingStatus.REJECTED);
        firstBooking.setBooker(booker);
        firstBooking.setItem(bookedItem);
        bookingRepository.save(firstBooking);

        Booking secondBooking = new Booking();
        secondBooking.setStartDate(LocalDateTime.now().minusHours(10));
        secondBooking.setEndDate(LocalDateTime.now().minusHours(5));
        secondBooking.setStatus(BookingStatus.APPROVED);
        secondBooking.setBooker(booker);
        secondBooking.setItem(bookedItem);
        bookingRepository.save(secondBooking);


        List<Booking> found = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(
                booker.getId(), BookingStatus.REJECTED, PageRequest.of(page, size));


        assertEquals(expectedSize, found.size());
        assertEquals(firstBooking.getId(), found.get(0).getId());
        assertEquals(firstBooking.getStartDate(), found.get(0).getStartDate());
        assertEquals(firstBooking.getEndDate(), found.get(0).getEndDate());
        assertEquals(firstBooking.getStatus(), found.get(0).getStatus());
        assertEquals(firstBooking.getBooker().getId(), found.get(0).getBooker().getId());
        assertEquals(firstBooking.getItem().getId(), found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByItem_Owner_IdOrderByStartDateDescTest() {
        int expectedSize = 1;

        int page = 0;
        int size = 10;

        User booker = new User();
        booker.setName("test-booker-name-1");
        booker.setEmail("test-booker-email-1");
        userRepository.save(booker);

        User itemOwner = new User();
        booker.setName("test-booker-name-2");
        booker.setEmail("test-booker-email-2");
        userRepository.save(itemOwner);

        Item bookedItem = new Item();
        bookedItem.setName("test-item-name-1");
        bookedItem.setDescription("test-item-desc-1");
        boolean isAvailable = true;
        bookedItem.setAvailable(isAvailable);
        bookedItem.setOwner(itemOwner);
        itemRepository.save(bookedItem);

        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(5));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(bookedItem);
        bookingRepository.save(booking);


        List<Booking> found = bookingRepository.findAllByItem_Owner_IdOrderByStartDateDesc(
                itemOwner.getId(), PageRequest.of(page, size));

        assertEquals(expectedSize, found.size());
        assertEquals(booking.getId(), found.get(0).getId());
        assertEquals(booking.getStartDate(), found.get(0).getStartDate());
        assertEquals(booking.getEndDate(), found.get(0).getEndDate());
        assertEquals(booking.getStatus(), found.get(0).getStatus());
        assertEquals(booking.getBooker().getId(), found.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByItem_Owner_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDescTest() {
        int expectedSize = 1;

        int page = 0;
        int size = 10;

        User booker = new User();
        booker.setName("test-booker-name-1");
        booker.setEmail("test-booker-email-1");
        userRepository.save(booker);

        User itemOwner = new User();
        booker.setName("test-booker-name-2");
        booker.setEmail("test-booker-email-2");
        userRepository.save(itemOwner);

        Item firstItem = new Item();
        firstItem.setName("test-item-name-1");
        firstItem.setDescription("test-item-desc-1");
        boolean isAvailable = true;
        firstItem.setAvailable(isAvailable);
        firstItem.setOwner(itemOwner);
        itemRepository.save(firstItem);

        Item secondItem = new Item();
        secondItem.setName("test-item-name-2");
        secondItem.setDescription("test-item-desc-2");
        secondItem.setAvailable(isAvailable);
        secondItem.setOwner(booker);
        itemRepository.save(secondItem);

        Booking firstBooking = new Booking();
        firstBooking.setStartDate(LocalDateTime.now().minusHours(1));
        firstBooking.setEndDate(LocalDateTime.now().plusHours(5));
        firstBooking.setStatus(BookingStatus.APPROVED);
        firstBooking.setBooker(booker);
        firstBooking.setItem(firstItem);
        bookingRepository.save(firstBooking);

        Booking secondBooking = new Booking();
        secondBooking.setStartDate(LocalDateTime.now().minusHours(10));
        secondBooking.setEndDate(LocalDateTime.now().minusHours(5));
        secondBooking.setStatus(BookingStatus.APPROVED);
        secondBooking.setBooker(booker);
        secondBooking.setItem(secondItem);
        bookingRepository.save(secondBooking);


        List<Booking> found = bookingRepository.findAllByItem_Owner_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(
                itemOwner.getId(), LocalDateTime.now(),
                LocalDateTime.now(), PageRequest.of(page, size));


        assertEquals(expectedSize, found.size());
        assertEquals(firstBooking.getId(), found.get(0).getId());
        assertEquals(firstBooking.getStartDate(), found.get(0).getStartDate());
        assertEquals(firstBooking.getEndDate(), found.get(0).getEndDate());
        assertEquals(firstBooking.getStatus(), found.get(0).getStatus());
        assertEquals(firstBooking.getBooker().getId(), found.get(0).getBooker().getId());
        assertEquals(firstBooking.getItem().getId(), found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByItem_Owner_IdAndEndDateIsBeforeOrderByStartDateDescTest() {
        int expectedSize = 1;

        int page = 0;
        int size = 10;

        User booker = new User();
        booker.setName("test-booker-name-1");
        booker.setEmail("test-booker-email-1");
        userRepository.save(booker);

        User itemOwner = new User();
        booker.setName("test-booker-name-2");
        booker.setEmail("test-booker-email-2");
        userRepository.save(itemOwner);

        Item firstItem = new Item();
        firstItem.setName("test-item-name-1");
        firstItem.setDescription("test-item-desc-1");
        boolean isAvailable = true;
        firstItem.setAvailable(isAvailable);
        firstItem.setOwner(itemOwner);
        itemRepository.save(firstItem);

        Item secondItem = new Item();
        secondItem.setName("test-item-name-2");
        secondItem.setDescription("test-item-desc-2");
        secondItem.setAvailable(isAvailable);
        secondItem.setOwner(booker);
        itemRepository.save(secondItem);

        Booking firstBooking = new Booking();
        firstBooking.setStartDate(LocalDateTime.now().minusHours(10));
        firstBooking.setEndDate(LocalDateTime.now().plusHours(5));
        firstBooking.setStatus(BookingStatus.APPROVED);
        firstBooking.setBooker(booker);
        firstBooking.setItem(secondItem);
        bookingRepository.save(firstBooking);

        Booking secondBooking = new Booking();
        secondBooking.setStartDate(LocalDateTime.now().minusHours(10));
        secondBooking.setEndDate(LocalDateTime.now().minusHours(5));
        secondBooking.setStatus(BookingStatus.APPROVED);
        secondBooking.setBooker(booker);
        secondBooking.setItem(firstItem);
        bookingRepository.save(secondBooking);


        List<Booking> found = bookingRepository.findAllByItem_Owner_IdAndEndDateIsBeforeOrderByStartDateDesc(
                itemOwner.getId(), LocalDateTime.now(), PageRequest.of(page, size));


        assertEquals(expectedSize, found.size());
        assertEquals(secondBooking.getId(), found.get(0).getId());
        assertEquals(secondBooking.getStartDate(), found.get(0).getStartDate());
        assertEquals(secondBooking.getEndDate(), found.get(0).getEndDate());
        assertEquals(secondBooking.getStatus(), found.get(0).getStatus());
        assertEquals(secondBooking.getBooker().getId(), found.get(0).getBooker().getId());
        assertEquals(secondBooking.getItem().getId(), found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByItem_Owner_IdAndStartDateIsAfterOrderByStartDateDescTest() {
        int expectedSize = 1;

        int page = 0;
        int size = 10;

        User booker = new User();
        booker.setName("test-booker-name-1");
        booker.setEmail("test-booker-email-1");
        userRepository.save(booker);

        User itemOwner = new User();
        booker.setName("test-booker-name-2");
        booker.setEmail("test-booker-email-2");
        userRepository.save(itemOwner);

        Item firstItem = new Item();
        firstItem.setName("test-item-name-1");
        firstItem.setDescription("test-item-desc-1");
        boolean isAvailable = true;
        firstItem.setAvailable(isAvailable);
        firstItem.setOwner(itemOwner);
        itemRepository.save(firstItem);

        Item secondItem = new Item();
        secondItem.setName("test-item-name-2");
        secondItem.setDescription("test-item-desc-2");
        secondItem.setAvailable(isAvailable);
        secondItem.setOwner(booker);
        itemRepository.save(secondItem);

        Booking firstBooking = new Booking();
        firstBooking.setStartDate(LocalDateTime.now().plusHours(1));
        firstBooking.setEndDate(LocalDateTime.now().plusHours(5));
        firstBooking.setStatus(BookingStatus.APPROVED);
        firstBooking.setBooker(booker);
        firstBooking.setItem(firstItem);
        bookingRepository.save(firstBooking);

        Booking secondBooking = new Booking();
        secondBooking.setStartDate(LocalDateTime.now().minusHours(10));
        secondBooking.setEndDate(LocalDateTime.now().minusHours(5));
        secondBooking.setStatus(BookingStatus.APPROVED);
        secondBooking.setBooker(booker);
        secondBooking.setItem(secondItem);
        bookingRepository.save(secondBooking);


        List<Booking> found = bookingRepository.findAllByItem_Owner_IdAndStartDateIsAfterOrderByStartDateDesc(
                itemOwner.getId(), LocalDateTime.now(), PageRequest.of(page, size));


        assertEquals(expectedSize, found.size());
        assertEquals(firstBooking.getId(), found.get(0).getId());
        assertEquals(firstBooking.getStartDate(), found.get(0).getStartDate());
        assertEquals(firstBooking.getEndDate(), found.get(0).getEndDate());
        assertEquals(firstBooking.getStatus(), found.get(0).getStatus());
        assertEquals(firstBooking.getBooker().getId(), found.get(0).getBooker().getId());
        assertEquals(firstBooking.getItem().getId(), found.get(0).getItem().getId());
    }

    @Test
    @SneakyThrows
    void findAllByItem_Owner_IdAndStatusOrderByStartDateDescTest() {
        int expectedSize = 1;

        int page = 0;
        int size = 10;

        User booker = new User();
        booker.setName("test-booker-name-1");
        booker.setEmail("test-booker-email-1");
        userRepository.save(booker);

        User itemOwner = new User();
        booker.setName("test-booker-name-2");
        booker.setEmail("test-booker-email-2");
        userRepository.save(itemOwner);

        Item firstItem = new Item();
        firstItem.setName("test-item-name-1");
        firstItem.setDescription("test-item-desc-1");
        boolean isAvailable = true;
        firstItem.setAvailable(isAvailable);
        firstItem.setOwner(itemOwner);
        itemRepository.save(firstItem);

        Item secondItem = new Item();
        secondItem.setName("test-item-name-2");
        secondItem.setDescription("test-item-desc-2");
        secondItem.setAvailable(isAvailable);
        secondItem.setOwner(booker);
        itemRepository.save(secondItem);

        Booking firstBooking = new Booking();
        firstBooking.setStartDate(LocalDateTime.now().plusHours(1));
        firstBooking.setEndDate(LocalDateTime.now().plusHours(5));
        firstBooking.setStatus(BookingStatus.REJECTED);
        firstBooking.setBooker(booker);
        firstBooking.setItem(firstItem);
        bookingRepository.save(firstBooking);

        Booking secondBooking = new Booking();
        secondBooking.setStartDate(LocalDateTime.now().minusHours(10));
        secondBooking.setEndDate(LocalDateTime.now().minusHours(5));
        secondBooking.setStatus(BookingStatus.APPROVED);
        secondBooking.setBooker(booker);
        secondBooking.setItem(secondItem);
        bookingRepository.save(secondBooking);


        List<Booking> found = bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(
                itemOwner.getId(), BookingStatus.REJECTED, PageRequest.of(page, size));


        assertEquals(expectedSize, found.size());
        assertEquals(firstBooking.getId(), found.get(0).getId());
        assertEquals(firstBooking.getStartDate(), found.get(0).getStartDate());
        assertEquals(firstBooking.getEndDate(), found.get(0).getEndDate());
        assertEquals(firstBooking.getStatus(), found.get(0).getStatus());
        assertEquals(firstBooking.getBooker().getId(), found.get(0).getBooker().getId());
        assertEquals(firstBooking.getItem().getId(), found.get(0).getItem().getId());
    }
}
