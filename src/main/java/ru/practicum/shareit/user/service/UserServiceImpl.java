package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserMemoryRepository;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMemoryRepository userMemoryRepository;

    @Override
    public UserDto addUser(UserCreateRequestDto userCreate) {
        log.debug("Метод сервиса. Добавление пользователя {}", userCreate);
        if (userMemoryRepository.containsEmail(userCreate.getEmail())) {
            throw new ConflictException(userCreate.getEmail() + " этот email уже используется");
        }
        User user = UserMapper.toUser(userCreate);
        user.setId(userMemoryRepository.addUser(user));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserUpdateRequestDto userDto, Long userId) {
        log.debug("Метод сервиса. Обновление пользователя {} с id = {}", userDto, userId);
        User user = getUserOrThrow(userId);

        if (userDto.getEmail() != null) {

            if (!userDto.getEmail().equals(user.getEmail()) && userMemoryRepository.containsEmail(userDto.getEmail())) {
                throw new ConflictException(userDto.getEmail() + " этот email уже используется");
            }
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        userMemoryRepository.updateUser(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public boolean deleteUser(long userId) {
        log.debug("Метод сервиса. Удаление пользователя с id {}", userId);
        getUserOrThrow(userId);
        return userMemoryRepository.deleteUser(userId);
    }

    @Override
    public UserDto getUser(long userId) {
        log.debug("Метод сервиса. Получение пользователя с id {}", userId);
        return UserMapper.toUserDto(getUserOrThrow(userId));
    }

    private User getUserOrThrow(long userId) {
        return userMemoryRepository
                .getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id = " + userId));
    }
}
