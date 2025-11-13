package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(ItemCreateDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, Long userId, ItemUpdateRequestDto itemDto);

    ItemDto getItem(Long itemId, Long userId);

    Collection<ItemDto> getAllUserItems(Long userId);

    Collection<ItemDto> searchItems(String text);

    CommentDto addComment(long itemId, long userId, CommentCreateRequestDto comment);

}
