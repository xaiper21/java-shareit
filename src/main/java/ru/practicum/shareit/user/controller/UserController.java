package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.service.UserService;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@ResponseBody
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserCreateRequestDto user) {
        log.debug("Метод контроллера. Добавление пользователя {}", user);
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Valid @RequestBody UserUpdateRequestDto user,
                              @Positive @NotNull @PathVariable Long userId) {
        log.debug("Метод контроллера. Обновление пользователя {} с id {}", user, userId);
        return userService.updateUser(user, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.debug("Метод контроллера. Получение пользователя с id{}", userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public boolean deleteUser(@PathVariable Long userId) {
        log.debug("Метод контроллера. Удаление пользователя с id {}", userId);
        return userService.deleteUser(userId);
    }
}
