package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerItem {
    private long id;
    private String name;
    private long ownerId;
}
