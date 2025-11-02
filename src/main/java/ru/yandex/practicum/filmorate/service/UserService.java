package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        validateUser(user);
        ensureName(user);
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        getById(user.getId());
        validateUser(user);
        ensureName(user);
        return userStorage.update(user);
    }

    private void ensureName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User getById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public void addFriends(int id, int friendId) {
        User user = getById(id);
        User friend = getById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        log.info("Пользователи {} и {} теперь друзья.", id, friendId);
    }

   public void removeFriend(int id, int friendId) {
       User user = getById(id);
       User friend = getById(friendId);

       user.getFriends().remove(friendId);
       friend.getFriends().remove(id);
       log.info("Пользователи {} и {} больше не друзья.", id, friendId);
   }

   public List<User> getFriends(int id) {
       User user = getById(id);
       return user.getFriends()
               .stream()
               .map(userStorage::getById)
               .flatMap(Optional::stream)
               .collect(Collectors.toList());
   }

    public List<User> getCommonFriends(int id, int otherId) {
        User user = getById(id);
        User otherUser = getById(otherId);

        Set<Integer> commonFriendIds = new HashSet<>(user.getFriends());

        commonFriendIds.retainAll(otherUser.getFriends());

        return commonFriendIds.stream()
                .map(userStorage::getById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
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
    }
}