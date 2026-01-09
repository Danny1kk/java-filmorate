package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class MpaController {
    private final RatingService ratingService;

    @GetMapping("/mpa")
    public Collection<MpaDto> getAllMpa() {
        return ratingService.findAll();
    }

    @GetMapping("/mpa/{id}")
    public  MpaDto getMpaById(@PathVariable int id) {
        return ratingService.getById(id);
    }
}