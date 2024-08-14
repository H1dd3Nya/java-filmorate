package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.annotation.Update;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    @DisplayName("Валидация названия")
    void validateName() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.now());
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Валидация описания")
    void validateDescription() {
        Film film = new Film();
        film.setName("Joker");
        film.setReleaseDate(LocalDate.now());
        film.setDescription("a".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Валидация даты релиза")
    void validateReleaseDate() {
        Film film = new Film();
        film.setName("Batman");
        film.setDescription("12345");
        film.setReleaseDate(LocalDate.MIN);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Валидация продолжительности")
    void validateDuration() {
        Film film = new Film();
        film.setName("Robin");
        film.setDescription("Round");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Валидация всех полей")
    void validateAllFields() {
        Film film = new Film();
        film.setName("Robin");
        film.setDescription("Round");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Валидация id")
    void validateId() {
        Film film = new Film();
        film.setId(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Update.class);

        assertFalse(violations.isEmpty());
    }
}