package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AnswerItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setCreated(itemRequest.getCreated());
        dto.setDescription(itemRequest.getDescription());
        return dto;
    }

    public static ItemRequestWithAnswersDto toDtoWithAnswers(ItemRequest itemRequest) {
        ItemRequestWithAnswersDto dto = new ItemRequestWithAnswersDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        dto.setItems(itemRequest.getItems() != null ? itemRequest.getItems().stream()
                .map(ItemRequestMapper::toAnswerItemDto)
                .collect(Collectors.toList()) : new ArrayList<>());
        return dto;
    }

    public static List<ItemRequestWithAnswersDto> toDtoWithAnswersList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::toDtoWithAnswers)
                .collect(Collectors.toList());
    }

    private static AnswerItemDto toAnswerItemDto(Item item) {
        AnswerItemDto dto = new AnswerItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());
        return dto;
    }
}
