package ru.yandex.practicum.filmorate.dal.dto.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.dal.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dal.dto.mpa.MpaDto;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private MpaDto mpa;
    private Set<GenreDto> genres;

    public boolean isNameValid() {
        return name != null && !name.isBlank();
    }

    public boolean isDescriptionValid() {
        return description != null && !description.isBlank();
    }

    public boolean isReleaseDateValid() {
        return releaseDate != null;
    }

    public boolean isDurationValid() {
        return  duration != null;
    }

    public boolean isMpaValid() {
        return  mpa != null && mpa.getId() != null;
    }

    public  boolean isGenresValid() {
        return  genres != null && !genres.isEmpty();
    }
}