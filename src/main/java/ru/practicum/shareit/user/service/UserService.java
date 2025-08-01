package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    UserDto addUser(UserCreateRequestDto user);

    UserDto updateUser(UserUpdateRequestDto user, Long userId);

    boolean deleteUser(long userId);

    UserDto getUser(long userId);
}
