package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<GenreDto> findAll() {
        return genreStorage.getAllGenres().stream()
                .map(this::toDto)
                .toList();
    }

    public GenreDto getById(Integer id) {
        Genre genre = genreStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с таким id не найден: " + id));
        return toDto(genre);
    }

    private  GenreDto toDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }
}