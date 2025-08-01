package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class UserMemoryRepository implements UserRepository {
    Map<Long, User> users = new HashMap<>();
    long nextId = 0L;

    private long getNextId() {
        return ++nextId;
    }

    @Override
    public boolean containsEmail(String email) {
        if (users.isEmpty()) {
            return false;
        }
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long addUser(User user) {
        long userId = getNextId();
        user.setId(userId);
        users.put(userId, user);
        return userId;
    }

    @Override
    public boolean updateUser(User user) {
        User oldUser = users.get(user.getId());
        users.put(user.getId(), user);
        return !oldUser.equals(user);
    }

    @Override
    public boolean deleteUser(long userId) {
        boolean result = users.containsKey(userId);
        users.remove(userId);
        return result;
    }

    @Override
    public Optional<User> getUser(long userId) {
        return Optional.ofNullable(users.get(userId));
    }
}
