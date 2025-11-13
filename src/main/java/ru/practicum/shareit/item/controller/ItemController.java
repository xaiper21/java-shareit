package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/items")
@ResponseBody
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemCreateDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Метод контроллера. Добавление новой вещи {} где id пользователя {} requestId = {}",
                itemDto, userId);
        return itemService.addItem(itemDto, userId);
    }


    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemUpdateRequestDto itemDto) {
        log.debug("Метод контроллера. Обновление вещи {} где id пользователя {}, id вещи {}", itemId, userId, itemId);
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable("itemId") @Positive @NotNull Long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Метод контроллера. Получение вещи с id {} для пользователя с id {}", itemId, userId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        log.debug("Метод контроллера. Получение всех вещей пользователя с id {}", userId);
        return itemService.getAllUserItems(userId);
    }


    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam("text") String text) {
        log.debug("Метод контроллера. Поиск вещей по входящей строке {}", text);
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable("itemId") @Positive @NotNull Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Valid CommentCreateRequestDto comment) {
        return itemService.addComment(itemId, userId, comment);
    }
}
