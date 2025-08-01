package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {

    boolean containsEmail(String email);

    long addUser(User user);

    boolean updateUser(User user);

    boolean deleteUser(long userId);

    Optional<User> getUser(long userId);
}
