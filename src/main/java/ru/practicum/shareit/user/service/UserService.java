package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

public interface UserService {

    UserDto addUser(UserCreateRequestDto user);

    UserDto updateUser(UserUpdateRequestDto user, Long userId);

    boolean deleteUser(long userId);

    UserDto getUser(long userId);
}
