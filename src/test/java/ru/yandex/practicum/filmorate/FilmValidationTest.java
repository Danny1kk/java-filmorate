package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidationTest {

    private final FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));

    @Test
    void shouldThrowExceptionIfNameIsEmpty() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2006, 6, 6));

        ValidationException e = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Название фильма не может быть пустым", e.getMessage());
    }

    @Test
    void shouldThrowExceptionIfDurationIsNegative() {
        Film film = new Film();
        film.setName("Текст");
        film.setDescription("описание");
        film.setDuration(-160);
        film.setReleaseDate(LocalDate.of(1894, 12, 6));

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }
}