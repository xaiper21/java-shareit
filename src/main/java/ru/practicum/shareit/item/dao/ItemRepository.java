package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;


public interface ItemRepository {
    long add(Item item);

    boolean update (Item item);

    Optional<Item> get(long id);

    Collection<Item> getUserItems(Long userId);

    Collection<Item> search(String text);


}
