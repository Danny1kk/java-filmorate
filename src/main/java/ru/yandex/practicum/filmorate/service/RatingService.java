package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage ratingStorage;

    public Collection<MpaDto> findAll() {
        return ratingStorage.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public MpaDto getById(int id) {
        MpaRating rating = ratingStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id = " + id + " не найден"));
        return toDto(rating);
    }

    private MpaDto toDto(MpaRating rating) {
        MpaDto dto = new MpaDto();
        dto.setId(rating.getId());
        dto.setName(rating.getName());
        return dto;
    }
}