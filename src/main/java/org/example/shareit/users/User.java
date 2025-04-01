package org.example.shareit.users;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.shareit.bookings.Booking;
import org.example.shareit.items.comments.Comment;
import org.example.shareit.items.Item;
import org.example.shareit.requests.Request;

import java.util.ArrayList;
import java.util.List;

@Data
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

    @OneToMany(mappedBy = "requester")
    List<Request> requests = new ArrayList<>();
}
