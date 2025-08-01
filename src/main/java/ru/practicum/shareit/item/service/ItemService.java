package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, Long userId, ItemUpdateRequestDto itemDto);

    ItemDto getItemForAll(Long itemId);

    Collection<ItemDto> getAllUserItems(Long userId);

    Collection<ItemDto> searchItems(String text);

}
