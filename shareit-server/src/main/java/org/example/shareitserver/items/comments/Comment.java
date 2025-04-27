package org.example.shareitserver.items.comments;

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
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String text;

    @ManyToOne
    Item item;

    @ManyToOne
    User author;
    
    LocalDateTime created;
}
