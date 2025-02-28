package org.example.shareit.item;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.shareit.booking.Booking;
import org.example.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String name;

    String description;

    @Column(name = "is_available")
    boolean available;

    @ManyToOne
    User owner;

    @OneToMany(mappedBy = "item")
    List<Booking> bookings = new ArrayList<>();
}
