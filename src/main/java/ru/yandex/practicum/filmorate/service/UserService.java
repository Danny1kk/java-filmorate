package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    //private final Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User getById(int id) {
        return userStorage.getById(id);
    }

    public void addFriends(int id, int friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Друг с id=" + friendId + " не найден");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(id); // Взаимное добавление, если дружба двусторонняя
        log.info("Пользователи {} и {} теперь друзья.", id, friendId);
    }

   public void removeFriend(int id, int friendId) {
       User user = getById(id);
       User friend = getById(friendId);

       if (user == null) {
           throw new NotFoundException("Пользователь с id=" + id + " не найден");
       }
       if (friend == null) {
           throw new NotFoundException("Друг с id=" + friendId + " не найден");
       }

       user.getFriends().remove(friendId);
       friend.getFriends().remove(id); // Удаляем взаимную связь, если она была добавлена
       log.info("Пользователи {} и {} больше не друзья.", id, friendId);
   }

   public List<User> getFriends(int id) {
       User user = userStorage.getById(id);

       if (user == null) {
           throw new NotFoundException("Пользователь с id=" + id + " не найден");
       }

        return user.getFriends()
                .stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
   }

    public List<User> getCommonFriends(int id, int otherId) {
        User user = userStorage.getById(id);
        User otherUser = userStorage.getById(otherId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        if (otherUser == null) {
            throw new NotFoundException("Пользователь с id=" + otherId + " не найден");
        }

        Set<Integer> commonFriendIds = new HashSet<>(user.getFriends());

        commonFriendIds.retainAll(otherUser.getFriends());

        return commonFriendIds.stream()
                .map(userStorage::getById)
                .filter(u -> u != null)
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