package org.example.shareit.requests;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.shareit.item.Item;
import org.example.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String description;

    LocalDateTime created;

    @ManyToOne
    User requestor;

    @OneToMany(mappedBy = "request")
    List<Item> responseItems = new ArrayList<>();
}
