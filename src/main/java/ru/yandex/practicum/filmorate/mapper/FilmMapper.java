package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dal.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dal.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dal.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dal.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dal.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        fillCommonFields(film, request.getName(), request.getDescription(),
                request.getReleaseDate(), request.getDuration(),
                request.getMpa(), request.getGenres());
        return film;
    }

    public static Film mapToFilm(UpdateFilmRequest request) {
        Film film = new Film();
        film.setId(request.getId());
        fillCommonFields(film, request.getName(), request.getDescription(),
                request.getReleaseDate(), request.getDuration(),
                request.getMpa(), request.getGenres());
        return film;
    }

    public static FilmDto mapToFilmDto(Film film, Collection<MpaRating> allMpa,
                                       Collection<Genre> allGenre) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        if (film.getRatingId() != null) {
           allMpa.stream()
                    .filter(m -> Objects.equals(m.getId(), film.getRatingId()))
                    .findFirst()
                    .ifPresent(m -> {
                        MpaDto mpaDto = new MpaDto();
                        mpaDto.setName(m.getName());
                        mpaDto.setId(m.getId());
                        dto.setMpa(mpaDto);
                    });
        }

        if (film.getGenreIds() != null && !film.getGenreIds().isEmpty()) {
            Set<GenreDto> genreIds = film.getGenreIds().stream()
                    .map(genreId -> {
                        GenreDto g = new GenreDto();
                        g.setId(genreId);

                        allGenre.stream()
                                .filter(genre -> genre.getId().equals(genreId))
                                .findFirst()
                                .ifPresent(genre -> g.setName(genre.getName()));

                        return g;
                    })
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            dto.setGenres(genreIds);
        }

        dto.setLikes(film.getLikes() != null ? film.getLikes().size() : 0);

        return dto;
    }

     private static void fillCommonFields(Film film, String name, String description,
                                          LocalDate releaseDate, Integer duration,
                                          MpaDto mpa, Set<GenreDto> genres) {
         film.setName(name);
         film.setDescription(description);
         film.setReleaseDate(releaseDate);
         film.setDuration(duration);

         if (mpa != null) {
             film.setRatingId(mpa.getId());
         }

         if (genres != null) {
             film.setGenreIds(genres.stream()
                     .map(GenreDto::getId)
                     .collect(Collectors.toSet()));
         }
     }
}