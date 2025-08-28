package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentCreateRequestDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}
