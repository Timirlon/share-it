package org.example.shareitserver.requests;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.shareitserver.items.Item;
import org.example.shareitserver.users.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
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
    User requester;

    @OneToMany(mappedBy = "request")
    List<Item> responseItems = new ArrayList<>();
}
