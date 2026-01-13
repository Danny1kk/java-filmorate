package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dal.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dal.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dal.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.*;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public FilmDto addFilm(@RequestBody @Valid NewFilmRequest request) {
        return filmService.addFilm(request);
    }

    @PutMapping
    public FilmDto updateFilm(@RequestBody @Valid UpdateFilmRequest request) {
        return filmService.updateFilm(request);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @GetMapping("/{id}")
    public FilmDto getById(@PathVariable long id) {
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }
}