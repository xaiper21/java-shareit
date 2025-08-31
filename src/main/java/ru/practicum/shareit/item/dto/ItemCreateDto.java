package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class ItemCreateDto {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Available cannot be null")
    private Boolean available;

    @Nullable
    @Positive(message = "Request ID must be positive")
    private Long requestId;
}