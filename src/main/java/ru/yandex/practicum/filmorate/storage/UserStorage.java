package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    Optional<User> getById(long id);

    Collection<User> getAllUsers();

    void delete(long id);

    void addFriends(long userId, long friendsId);

    void removeFriends(long userId, long friendsId);

    List<User> getFriends(long userId);
}