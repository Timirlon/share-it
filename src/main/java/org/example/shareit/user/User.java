package org.example.shareit.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.shareit.booking.Booking;
import org.example.shareit.item.Comment;
import org.example.shareit.item.Item;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String name;

    String email;

    @OneToMany(mappedBy = "owner")
    List<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "booker")
    List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    List<Comment> comments = new ArrayList<>();
}
