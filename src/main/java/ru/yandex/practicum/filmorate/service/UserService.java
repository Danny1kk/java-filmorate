package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User getById(int id) {
        return userStorage.getById(id);
    }

    public void addFriends(int id, int friendId) {
        friends.computeIfAbsent(id, k -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(id);
    }

   public void remoteFriend(int id, int friendId) {
        friends.getOrDefault(id, new HashSet<>()).remove(friendId);
        friends.getOrDefault(friendId, new HashSet<>()).remove(id);
   }

   public List<User> getFriends(int id) {
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
}