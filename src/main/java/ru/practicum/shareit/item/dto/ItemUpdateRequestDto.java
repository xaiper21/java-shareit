package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemUpdateRequestDto {
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
}
