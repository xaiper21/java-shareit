package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.*;
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
    UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserCreateRequestDto user) {
        log.debug("Adding user {}", user);
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Valid @RequestBody UserUpdateRequestDto user,
                              @Positive @NotNull @PathVariable Long userId) {
        log.debug("Updating user {} and userId {}", user, userId);
        return userService.updateUser(user, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.debug("Getting user by id {}", userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public boolean deleteUser(@PathVariable Long userId) {
        log.debug("Deleting user by id {}", userId);
        return userService.deleteUser(userId);
    }
}
