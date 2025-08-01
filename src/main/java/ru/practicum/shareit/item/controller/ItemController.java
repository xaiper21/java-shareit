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
    private ItemService itemService;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Adding new item  {} and headerId {}", itemDto, userId);
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemUpdateRequestDto itemDto) {
        log.debug("Updating item {} and headerId {}, item id {}", itemId, userId, itemId);
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemForAll(@PathVariable("itemId") Long itemId) {
        log.debug("Getting item {} for all", itemId);
        return itemService.getItemForAll(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        log.debug("Getting all user items for user {}", userId);
        return itemService.getAllUserItems(userId);
    }


    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam("text") String text) {
        log.debug("Searching items for {}", text);
        return itemService.searchItems(text);
    }
}
