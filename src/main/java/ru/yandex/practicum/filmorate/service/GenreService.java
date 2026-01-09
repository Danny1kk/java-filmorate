package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreStorage;

    public Collection<Genre> findAll() {
        return genreStorage.getAllGenres();
    }

    public Genre getById(Integer id) {
        return genreStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с таким id не найден: " + id));
    }
}