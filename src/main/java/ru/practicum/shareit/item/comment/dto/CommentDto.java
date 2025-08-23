package ru.practicum.shareit.item.comment.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private ItemDto item;
    private LocalDateTime created;
}
