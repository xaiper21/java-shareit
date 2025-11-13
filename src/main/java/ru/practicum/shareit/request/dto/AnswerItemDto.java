package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class AnswerItemDto {
    private long id;
    private String name;
    private long ownerId;
}
