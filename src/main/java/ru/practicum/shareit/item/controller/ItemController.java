package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Метод контроллера. Добавление новой вещи {} где id пользователя {}", itemDto, userId);
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
    public ItemDto getItem(@PathVariable("itemId") Long itemId) {
        log.debug("Метод контроллера. Получени вещи с id {} ", itemId);
        return itemService.getItem(itemId);
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
}
