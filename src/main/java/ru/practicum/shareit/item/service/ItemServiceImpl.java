package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        log.debug("Adding new item in service method");
        User user = userRepository.getUser(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        item.setId(itemRepository.add(item));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemUpdateRequestDto itemDto) {
        log.debug("Updating item in service method");
        Item item = itemRepository.get(itemId).orElseThrow(() -> new NotFoundException("объект не найден"));
        if (item.getOwner().getId() != userId) throw new NotFoundException("объект не найден");

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        itemRepository.update(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemForAll(Long itemId) {
        log.debug("Getting item for all in service method");
        return ItemMapper.toItemDto(itemRepository.get(itemId)
                .orElseThrow(() -> new NotFoundException("объект не найден")));
    }

    @Override
    public Collection<ItemDto> getAllUserItems(Long userId) {
        log.debug("Getting all user items in service method");
        userRepository.getUser(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return itemRepository.getUserItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        log.debug("Searching items in service method");
        return itemRepository.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
