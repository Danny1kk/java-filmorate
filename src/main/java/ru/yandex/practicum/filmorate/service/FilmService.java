package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dal.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dal.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.RatingDbStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    public FilmService(@Qualifier("filmsDb") FilmStorage filmStorage,
                       @Qualifier("userDb") UserStorage userStorage,
                       RatingDbStorage mpaStorage, GenreDbStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    private FilmDto toDto(Film film) {
        List<MpaRating> allMpa = mpaStorage.findAll();
        Collection<Genre> allGenres = genreStorage.getAllGenres();
        return FilmMapper.mapToFilmDto(film, allMpa, allGenres);
    }

    public Collection<FilmDto> getAllFilms() {
        List<MpaRating> allMpa = mpaStorage.findAll();
        Collection<Genre> allGenres = genreStorage.getAllGenres();
        return filmStorage.getAllFilms().stream()
                .map(film -> FilmMapper.mapToFilmDto(film, allMpa, allGenres))
                .toList();
    }

    public FilmDto addFilm(NewFilmRequest request) {
        Film film = FilmMapper.mapToFilm(request);
        validateFilm(film);
        Film saved = filmStorage.addFilm(film);
        return toDto(saved);
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        Film film = filmStorage.getById(request.getId())
                        .orElseThrow(NotFoundException::new);
        Film updatedFilms = FilmMapper.updateFilmFields(film, request);
        validateFilm(updatedFilms);
        updatedFilms = filmStorage.updateFilm(updatedFilms);
        return toDto(updatedFilms);
    }

    public FilmDto getById(long id) {
        Film film = filmStorage.getById(id)
                .orElseThrow(NotFoundException::new);
        return toDto(film);
    }

    public void addLike(int filmId, int userId) {
        filmStorage.getById(filmId).orElseThrow(NotFoundException::new);
        userStorage.getById(userId).orElseThrow(NotFoundException::new);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.getById(filmId).orElseThrow(NotFoundException::new);
        userStorage.getById(userId).orElseThrow(NotFoundException::new);
        filmStorage.removeLike(filmId, userId);
    }

    public List<FilmDto> getPopular(int count) {
        List<MpaRating> allMpa = mpaStorage.findAll();
        Collection<Genre> allGenres = genreStorage.getAllGenres();
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .map(film -> FilmMapper.mapToFilmDto(film, allMpa, allGenres))
                .toList();
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

        if(film.getRatingId() != null) {
            mpaStorage.getById(film.getRatingId())
                    .orElseThrow(() -> new NotFoundException
                            ("Система возрастных ограничений (рейтинг MPA) - не найден: " + film.getRatingId()));
        }

        if(film.getGenreIds() != null && !film.getGenreIds().isEmpty()) {
            for (Integer genreId : film.getGenreIds()) {
                genreStorage.getById(genreId)
                        .orElseThrow(() -> new NotFoundException("Жанры не найдены: " + genreId));
            }
        }
    }
}