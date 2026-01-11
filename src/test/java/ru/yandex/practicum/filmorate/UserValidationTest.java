
package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    InMemoryUserStorage userStorage;
    User user;

    @BeforeEach
    public  void setup() {
        userStorage = new InMemoryUserStorage();
        user = new User();
        user.setEmail("user@gmail.com");
        user.setLogin("Tot_samiy_user");
        user.setName("Danny");
        user.setBirthday(LocalDate.of(1992, 2, 10));
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@gmail.com")) {
            throw  new ValidationException("Email не может быть пустым и должен содержать символ '@'");
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

    private User addUser(User user) {
        validateUser(user);
        return  userStorage.addUser(user);
    }

    private User updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Пользователь не найден");
        }
        if (userStorage.getById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + user.getId() + " - не найден");
        }
        validateUser(user);
        return userStorage.updateUser(user);
    }

    @Test
    void shouldThrowExceptionIfEmailIsWrong() {
        user.setEmail(null);
        ValidationException e = assertThrows(ValidationException.class, () -> addUser(user));
        assertTrue(e.getMessage().contains("Электронная почта не может быть пустой и должна содержать символ @"));

        user.setEmail("wrongemail.ru");
        e = assertThrows(ValidationException.class, () -> addUser(user));
        assertTrue(e.getMessage().contains("Электронная почта не может быть пустой и должна содержать символ @"));
    }

    @Test
    void shouldUseLoginAsNameIfNameEmpty() {
        user.setEmail("user@gmail.com");
        user.setLogin("Tot_samiy_user");
        user.setName("");
        user.setBirthday(LocalDate.of(1992, 2, 10));

        User created = addUser(this.user);
        assertEquals(created.getName(), created.getLogin());
    }

    @Test
    void shouldSuccessfulAddUser() {
        User created = addUser(this.user);
        assertNotNull(created.getId());
        assertTrue(created.getId() > 0);
        assertEquals("user@gmail.com", created.getEmail());
        assertEquals("Tot_samiy_user", created.getLogin());
        assertEquals("Danny", created.getName());
        assertEquals(LocalDate.of(1992, 2, 10), created.getBirthday());
    }

    @Test
    void shouldSuccessfulWithInstantBirthday() {
        user.setBirthday(LocalDate.now());
        assertDoesNotThrow(() -> addUser(user));
    }

    @Test
    void shouldReturnValidationExceptionWithNullFields() {
        User user = new User();
        user.setEmail(null);
        user.setId(null);
        user.setLogin(null);
        user.setName(null);
        user.setBirthday(null);
        assertThrows(ValidationException.class, () -> addUser(user));
    }
}