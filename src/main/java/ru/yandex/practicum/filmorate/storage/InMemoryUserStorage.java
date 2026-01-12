package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friendships = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        friendships.put(user.getId(), new HashSet<>());
        log.info("Создан пользователь id={}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        users.put(newUser.getId(), newUser);
        log.info("Обновлён пользователь id={}", newUser.getId());
        return newUser;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
        friendships.values().forEach(getFriends -> getFriends.remove(id));
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addFriends(long userId, long friendsId) {
        friendships.computeIfAbsent(userId, k -> new HashSet<>()).add(friendsId);
        friendships.computeIfAbsent(friendsId, k -> new HashSet<>()).add(userId);
        log.info("Пользователи {} и {} стали друзьями", userId, friendsId);
    }

    @Override
    public void removeFriends(long userId, long friendsId) {
        if (friendships.containsKey(userId)) {
            friendships.get(userId).remove(friendsId);
        }
        if (friendships.containsKey(friendsId)) {
            friendships.get(friendsId).remove(userId);
        }
        log.info("Пользователи {} и {} больше не друзья", userId, friendsId);
    }

    @Override
    public List<User> getFriends(long userId) {
        Set<Long> friendsIds = friendships.getOrDefault(userId, Collections.emptySet());
        return friendsIds.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private long getNextId() {
        return users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}