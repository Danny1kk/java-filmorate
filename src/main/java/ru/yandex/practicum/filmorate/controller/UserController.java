package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    private void nameLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        validateUser(user);
        nameLogin(user);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с id=" + user.getId() + " не найден");
        }
        nameLogin(user);
        users.put(user.getId(), user);
        log.info("Пользователь обновлен: {}", user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен запрос на получение списка всех пользователей");
        return new ArrayList<>(users.values());
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: некорректный email {}", user.getEmail());
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