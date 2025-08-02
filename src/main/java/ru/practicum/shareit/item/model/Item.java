package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class Item {
    long id;
    String name;
    String description;
    boolean available;
    User owner;
    String request;
}
