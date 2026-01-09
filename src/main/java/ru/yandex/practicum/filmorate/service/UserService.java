package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dal.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dal.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDb") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto addUser(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);
        validateUser(user);
        User saved = userStorage.addUser(user);
        return UserMapper.mapToUserDto(saved);
    }

    public UserDto updateUserFields(UpdateUserRequest request) {
        User existing = userStorage.getById(request.getId())
                        .orElseThrow(NotFoundException::new);
        User updated = UserMapper.updateUserFields(existing, request);
        validateUser(updated);
        updated = userStorage.updateUser(updated);
        return UserMapper.mapToUserDto(updated);
    }

    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto getById(long id) {
        User user = userStorage.getById(id)
                .orElseThrow(NotFoundException::new);
        userStorage.loadFriends(user);
        return UserMapper.mapToUserDto(user);
    }

    public void delete(long id) {
        validateId(id);
        userStorage.delete(id);
    }

    public void addFriends(Long userId, long friendId) {
        if (userId.equals(friendId))
            throw new ValidationException("Нельзя добавить самого себя в друзья");

        userStorage.getById(userId).orElseThrow(() -> notFoundUser(userId));
        userStorage.getById(friendId).orElseThrow(() -> notFoundFriend(friendId));
        userStorage.addFriends(userId, friendId);
    }

   public void removeFriend(long userId, long friendId) {
       userStorage.getById(userId).orElseThrow(() -> notFoundUser(userId));
       userStorage.getById(friendId).orElseThrow(() -> notFoundFriend(friendId));
       userStorage.removeFriends(userId, friendId);
    }

   public List<UserDto> getFriends(long userId) {
       User user = userStorage.getById(userId)
               .orElseThrow(() -> notFoundUser(userId));
       userStorage.loadFriends(user);

       return user.getFriends().stream()
               .map(id -> {
                    User friend = userStorage.getById(id)
                            .orElseThrow(() -> notFoundUser(id));
                    userStorage.loadFriends(friend);
                    return UserMapper.mapToUserDto(friend);
               })
               .toList();
   }

    public Collection<User> getCommonFriends(long userId, long otherUserId) {
        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("Такой пользователь не найден"));
        userStorage.getById(otherUserId).orElseThrow(() -> new NotFoundException("Такой пользователь не найден"));

        Collection<User> commonFriends = userStorage.getCommonFriends(userId, otherUserId);

        if (commonFriends.isEmpty()) {
            throw new InternalError("Общих друзей не найдено");
        }

        return commonFriends;
    }

    private NotFoundException notFoundUser(long id) {
        return new NotFoundException("Пользователь не найден: " + id);
    }

    private NotFoundException notFoundFriend(long id) {
        return new NotFoundException("Друг не найден: " + id);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateId(long id) {
        if (getById(id) == null || id <= 0) {
            log.warn("Некорректный id: {}", id);
            throw new ValidationException("id должен быть > 0");
        }
    }
}