package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        getById(film.getId());
        return filmStorage.update(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film getById(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    public void addLike(int filmId, int userId) {
        Film film = getById(filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        film.addLike(userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getById(filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        film.removeLike(userId);
    }

    public List<Film> getPopular(int count) {
        if (count <= 0) {
            throw new ValidationException("Параметр count должен быть положительным");
        }
        return filmStorage.getPopular(count);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не должно превышать 200 символов");
        }
        LocalDate minDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(minDate)) {
            throw new ValidationException("Дата релиза должна быть указана и не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}