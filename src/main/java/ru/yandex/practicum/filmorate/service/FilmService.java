package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dal.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dal.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.RatingStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingStorage mpaStorage;
    private final GenreStorage genreStorage;

    private Film getFilmOrThrow(long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + id + "не найден"));
    }

    private void checkUserExists(long id) {
        userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + "не найден"));
    }

    public Collection<FilmDto> getAllFilms() {
        log.info("Запрос всех фильмов");
        return loadDetailsAndMapToDto(filmStorage.getAllFilms());
    }

    public FilmDto addFilm(NewFilmRequest request) {
        log.info("Добавление нового фильма: {}", request.getName());
        Film film = FilmMapper.mapToFilm(request);
        Film savedFilm = filmStorage.addFilm(film);
        return loadDetailsAndMapToDto(List.of(savedFilm)).get(0);
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        log.info("Обновление фильма с id = {}", request.getId());
        getFilmOrThrow(request.getId());

        Film film = FilmMapper.mapToFilm(request);
        Film updatedFilm = filmStorage.updateFilm(film);
        return loadDetailsAndMapToDto(List.of(updatedFilm)).get(0);
    }

    public FilmDto getById(long id) {
        log.info("Запрос фильма по id = {}", id);
        Film film = getFilmOrThrow(id);
        return loadDetailsAndMapToDto(List.of(film)).get(0);
    }

    public void addLike(int filmId, int userId) {
        log.info("Пользователь id = {} ставит лайк фильму id = {}", userId, filmId);
        getFilmOrThrow(filmId);
        checkUserExists(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        log.info("Пользователь id = {} удаляет лайк у фильма id = {}", userId, filmId);
        getFilmOrThrow(filmId);
        checkUserExists(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<FilmDto> getPopular(int count) {
        log.info("Запрос {} популярных фильмов", count);
        List<Film> popularFilms = filmStorage.getPopular(count);
        return loadDetailsAndMapToDto(popularFilms);
    }

    private List<FilmDto> loadDetailsAndMapToDto(List<Film> films) {
        if (films.isEmpty()) {
            return List.of();
        }

        List<MpaRating> allMpa = mpaStorage.findAll();
        List<Genre> allGenres = (List<Genre>) genreStorage.getAllGenres();

        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        Map<Long, Set<Integer>> genresMap = filmStorage.getGenresForFilms(filmIds);
        Map<Long, Set<Long>> likesMap = filmStorage.getLikesForFilms(filmIds);

        return films.stream()
                .map(film -> {
                    film.setGenreIds(genresMap.getOrDefault(film.getId(), Set.of()));
                    film.setLikes(likesMap.getOrDefault(film.getId(), Set.of()));
                    return FilmMapper.mapToFilmDto(film, allMpa, allGenres);
                }).toList();
    }
}