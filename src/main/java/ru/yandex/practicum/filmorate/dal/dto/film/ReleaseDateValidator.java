package ru.yandex.practicum.filmorate.dal.dto.film;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<AfterCinemaBirthday, LocalDate> {
    private static final LocalDate LOCAL_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(LOCAL_DATE);
    }
}