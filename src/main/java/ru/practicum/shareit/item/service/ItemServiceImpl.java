package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserMemoryRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserMemoryRepository userMemoryRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        log.debug("Метод сервиса. Добавление вещи {} пользователем с id {}", itemDto, userId);
        User user = getUserOrThrow(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        item.setId(itemRepository.add(item));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemUpdateRequestDto itemDto) {
        log.debug("Метод сервиса. Обновление вещи с id {} пользователем с id {}, данные обновления: {}",
                itemId, userId, itemDto);
        getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);
        if (item.getOwner().getId() != userId) {
            throw new ForbiddenException("У вас нет доступа к ресурсу");
        }

        if (checkStringNotNullAndNotEmpty(itemDto.getName())) item.setName(itemDto.getName());
        if (checkStringNotNullAndNotEmpty(itemDto.getDescription())) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        itemRepository.update(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        log.debug("Метод сервиса. Получение вещи с id {}", itemId);
        return ItemMapper.toItemDto(getItemOrThrow(itemId));
    }

    @Override
    public Collection<ItemDto> getAllUserItems(Long userId) {
        log.debug("Метод сервиса. Получение всех вещей пользователя с id {}", userId);
        getUserOrThrow(userId);
        return itemRepository.getUserItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        log.debug("Метод сервиса. Поиск вещей по входящей строке - {}", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long userId) {
        return userMemoryRepository
                .getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + "не найден!"));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository
                .get(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена!"));
    }

    private boolean checkStringNotNullAndNotEmpty(String value) {
        return value != null && !value.isBlank();
    }
}
