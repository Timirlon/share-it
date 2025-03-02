package org.example.shareit.item;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentMapper {
    public CommentReadDto toDto(Comment comment) {
        CommentReadDto dto = new CommentReadDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setItemName(comment.getItem().getName());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());

        return dto;
    }

    public Comment fromDto(CommentCreateDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());

        return comment;
    }

    public List<CommentReadDto> toDto(List<Comment> comments) {
        return comments.stream()
                .map(this::toDto)
                .toList();
    }
}
