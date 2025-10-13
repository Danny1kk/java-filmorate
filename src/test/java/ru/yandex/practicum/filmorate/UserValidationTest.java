package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidationTest {

    private final UserController userController = new UserController();

    @Test
    void shouldThrowExceptionIfEmailIsWrong() {
        User user = new User();
        user.setEmail("wrongemail.ru");
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(2026, 6, 6));

        ValidationException e = assertThrows(ValidationException.class, () -> userController.addUser(user));
        assertEquals("Некорректный email", e.getMessage());
    }

    @Test
    void shouldUseLoginAsNameIfNameEmpty() {
        User user = new User();
        user.setEmail("user@gmail.com");
        user.setLogin("Tot_samiy_user");
        user.setName("");
        user.setBirthday(LocalDate.of(1992, 2, 10));

        userController.addUser(user);
        assertEquals("Tot_samiy_user", user.getName());
    }
}