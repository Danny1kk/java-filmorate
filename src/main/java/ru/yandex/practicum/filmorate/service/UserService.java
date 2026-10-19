package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dal.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dal.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    private User getUserOrThrow(long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " - не найден"));
    }

    public UserDto addUser(NewUserRequest request) {
        log.info("Добавление нового пользователя: {}", request.getEmail());
        User user = UserMapper.mapToUser(request);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return UserMapper.mapToUserDto(userStorage.addUser(user));
    }

    public UserDto updateUserFields(UpdateUserRequest request) {
        log.info("Обновление пользователя с id = {}", request.getId());
        getUserOrThrow(request.getId());
        User user = UserMapper.mapToUser(request);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return UserMapper.mapToUserDto(userStorage.updateUser(user));
    }

    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(long id) {
        return UserMapper.mapToUserDto(getUserOrThrow(id));
    }

    public void addFriends(Long userId, long friendId) {
        log.info("Пользователь {} добавляет в друзья {}", userId, friendId);
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        userStorage.addFriends(userId, friendId);
    }

   public void removeFriend(long userId, long friendId) {
        log.info("Пользователь {} удаляет из друзей {}", userId, friendId);
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        userStorage.removeFriends(userId, friendId);
    }

   public List<UserDto> getFriends(long userId) {
        log.info("Запрос списка друзей пользователя {}", userId);
        getUserOrThrow(userId);
        return userStorage.getFriends(userId).stream()
               .map(UserMapper::mapToUserDto)
               .collect(Collectors.toList());
   }

    public List<UserDto> getCommonFriends(long userId, long otherUserId) {
        log.info("Запрос общих друзей пользователя {} и {}", userId, otherUserId);
        getUserOrThrow(userId);
        getUserOrThrow(otherUserId);

        List<User> userFriends = userStorage.getFriends(userId);
        List<User> otherFriends = userStorage.getFriends(otherUserId);

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}