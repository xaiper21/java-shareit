package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemUpdateRequestDto {
    String name;
    String description;
    Boolean available;
}
