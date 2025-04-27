package org.example.shareitserver.users;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.bookings.Booking;
import org.example.shareitserver.items.comments.Comment;
import org.example.shareitserver.items.Item;
import org.example.shareitserver.requests.Request;

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
