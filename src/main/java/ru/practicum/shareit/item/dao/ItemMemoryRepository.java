package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class ItemMemoryRepository implements ItemRepository {
    Map<Long, Item> items = new HashMap<>();
    Long nextId = 0L;

    private Long getNextId() {
        return ++nextId;
    }

    @Override
    public long add(Item item) {
        Long id = getNextId();
        item.setId(id);
        items.put(id, item);
        return id;
    }

    @Override
    public boolean update(Item item) {
        Item oldItem = items.get(item.getId());
        items.put(item.getId(), item);
        return !oldItem.equals(item);
    }

    @Override
    public Optional<Item> get(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> getUserItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return items.values().stream().filter(item ->
                        item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                                item.getName().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::isAvailable)
                .collect(Collectors.toList());
    }
}
