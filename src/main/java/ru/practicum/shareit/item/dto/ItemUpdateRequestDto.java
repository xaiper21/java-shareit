package ru.practicum.shareit.item.dto;

import lombok.Data;

//Изменить можно название,
// описание и статус доступа к аренде. Редактировать вещь может только её владелец.
@Data
public class ItemUpdateRequestDto {
    String name;
    String description;
    Boolean available;
}
