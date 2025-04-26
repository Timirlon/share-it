package org.example.shareitserver.items;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.bookings.Booking;
import org.example.shareitserver.items.comments.Comment;
import org.example.shareitserver.requests.Request;
import org.example.shareitserver.users.User;

import java.util.ArrayList;
import java.util.List;

@Data
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
    Boolean available;

    @ManyToOne
    User owner;

    @ManyToOne
    Request request;

    @OneToMany(mappedBy = "item")
    List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    List<Comment> comments = new ArrayList<>();

    @Transient
    Booking lastBooking;

    @Transient
    Booking nextBooking;
}
