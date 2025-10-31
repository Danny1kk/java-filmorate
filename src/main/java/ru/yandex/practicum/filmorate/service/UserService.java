package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();

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
        userStorage.getById(id);
        userStorage.getById(friendId);

        friends.computeIfAbsent(id, k -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(id);
    }

   public void removeFriend(int id, int friendId) {
       userStorage.getById(id);
       userStorage.getById(friendId);

       friends.getOrDefault(id, new HashSet<>()).remove(friendId);
       friends.getOrDefault(friendId, new HashSet<>()).remove(id);
   }

   public List<User> getFriends(int id) {
        getById(id);

        return friends.getOrDefault(id, Collections.emptySet())
                .stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
   }

   public List<User> getCommonFriends(int id, int otherId) {
        Set<Integer> common = new HashSet<>(friends.getOrDefault(id, Collections.emptySet()));
        common.retainAll(friends.getOrDefault(otherId, Collections.emptySet()));
        return common.stream()
                .map(userStorage::getById)
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