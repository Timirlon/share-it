package org.example.shareitgateway.bookings;

import org.example.shareitserver.bookings.BookingFilteringState;
import org.example.shareitserver.bookings.dtos.BookingCreateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

import static org.example.shareitgateway.utils.HeaderUtils.getUserIdRequestHeader;

@Component
public class BookingClient {
    private final RestTemplate restTemplate;

    public BookingClient(@Value("${shareitserver.server.url") String url,
                         RestTemplateBuilder builder) {

        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .build();
    }

    public ResponseEntity<Object> create(BookingCreateDto bookingDto, int userId) {
        HttpHeaders header = getUserIdRequestHeader(userId);

        return restTemplate.exchange(
                "/bookings",
                HttpMethod.POST,
                new HttpEntity<>(bookingDto, header),
                Object.class);
    }

    public ResponseEntity<Object> updateStatus(
            int bookingId, int userId, boolean isApproved) {

        HttpHeaders headers = getUserIdRequestHeader(userId);

        return restTemplate.exchange(
                "/bookings/{bookingId}?approved={approved}",
                HttpMethod.PATCH,
                new HttpEntity<>(null, headers),
                Object.class,
                Map.of("bookingId", bookingId, "approved", isApproved));
    }

    public ResponseEntity<Object> findById(int bookingId, int userId) {
        HttpHeaders header = getUserIdRequestHeader(userId);

        return restTemplate.exchange(
                "/bookings/{bookingId}",
                HttpMethod.GET,
                new HttpEntity<>(null, header),
                Object.class,
                Map.of("bookingId", bookingId)
        );
    }

    public ResponseEntity<Object> findMyBookings(
            int userId, BookingFilteringState state,
            int from, int size) {

        HttpHeaders header = getUserIdRequestHeader(userId);

        return restTemplate.exchange(
                "/bookings?state={state}&from={from}&size={size}",
                HttpMethod.GET,
                new HttpEntity<>(null, header),
                Object.class,
                Map.of("state", state, "from", from, "size", size)
        );
    }

    public ResponseEntity<Object> findBookingsOfMyItem(
            int userId, BookingFilteringState state,
            int from, int size) {

        HttpHeaders header = getUserIdRequestHeader(userId);

        return restTemplate.exchange(
                "/bookings/owner?state={state}&from={from}&size={size}",
                HttpMethod.GET,
                new HttpEntity<>(null, header),
                Object.class,
                Map.of("state", state, "from", from, "size", size)
        );
    }
}
