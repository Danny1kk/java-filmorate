package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    Optional<Film> getById(long id);

    List<Film> getAllFilms();

    List<Film> getPopular(int count);

    void delete(long id);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    Map<Long, Set<Integer>> getGenresForFilms(List<Long> filmId);

    Map<Long, Set<Long>> getLikesForFilms(List<Long> filmId);
}