package org.example.shareitserver.bookings;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.items.Item;
import org.example.shareitserver.users.User;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "start_date")
    LocalDateTime startDate;

    @Column(name = "end_date")
    LocalDateTime endDate;

    @ManyToOne
    Item item;

    @ManyToOne
    User booker;

    @Enumerated(value = EnumType.STRING)
    BookingStatus status;
}
