package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    public UserDto addUser(UserCreateRequestDto userCreate) {
        log.debug("Adding user {} in service", userCreate);
        if (userRepository.containsEmail(userCreate.getEmail())) {
            throw new ConflictException(userCreate.getEmail() + " already exists");
        }
        User user = UserMapper.toUser(userCreate);
        user.setId(userRepository.addUser(user));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserUpdateRequestDto userDto, Long userId) {
        log.debug("Updating user {}, userId = {} in service", userDto, userId);
        User user = userRepository.getUser(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (userDto.getEmail() != null) {

            if (!userDto.getEmail().equals(user.getEmail()) && userRepository.containsEmail(userDto.getEmail())) {
                throw new ConflictException(userDto.getEmail() + " already exists");
            }
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        userRepository.updateUser(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public boolean deleteUser(long userId) {
        log.debug("Deleting user by id {} in service", userId);
        return userRepository.deleteUser(userId);
    }

    @Override
    public UserDto getUser(long userId) {
        log.debug("Getting user by id {} in service", userId);
        return UserMapper.toUserDto(userRepository.getUser(userId).orElseThrow(()
                -> new NotFoundException("User not found")));
    }
}
