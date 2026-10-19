package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.rowmappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Component("userDbStorage")
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper mapper;

    @Override
    public User addUser(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String insertTableQuery = "INSERT INTO users(name, login, birthday, email)" + "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertTableQuery, new String[]{"user_id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getLogin());
            ps.setDate(3, Date.valueOf(user.getBirthday()));
            ps.setString(4, user.getEmail());
            return ps;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return  user;
    }

    @Override
    public User updateUser(User newUser) {
        String updateTableQuery = "UPDATE users SET name = ?, login = ?, birthday = ?, email = ?" +
                " WHERE user_id = ?";
        jdbcTemplate.update(updateTableQuery,
                newUser.getName(),
                newUser.getLogin(),
                Date.valueOf(newUser.getBirthday()),
                newUser.getEmail(),
                newUser.getId()
        );
        return  newUser;
    }

    @Override
    public void delete(long id) {
        String deleteTableQuery = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(deleteTableQuery, id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return  jdbcTemplate.query("SELECT * FROM users", mapper);
    }

    @Override
    public Optional<User> getById(long id) {
        try {
            User result = jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id = ?", mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void addFriends(long userId, long friendsId) {
        String insertGenreQuery = "INSERT INTO friends (user_id, friend_id) VALUES(?, ?)";
        jdbcTemplate.update(insertGenreQuery, userId, friendsId);
    }

    public void removeFriends(long userId, long friendsId) {
        String deleteGenreQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(deleteGenreQuery, userId, friendsId);
    }

    @Override
    public List<User> getFriends(long userId) {
        String query = "SELECT u.* FROM users u " +
                "JOIN friends f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(query, mapper, userId);
    }
}