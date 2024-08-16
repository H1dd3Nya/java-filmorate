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

class UserTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    @DisplayName("Валидация email")
    void validateEmail() {
        User user = new User();
        user.setName("12345");
        user.setEmail("это-неправильный?эмейл@");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Валидация логина")
    void validateLogin() {
        User user = new User();
        user.setName("123");
        user.setEmail("example@gmail.com");
        user.setBirthday(LocalDate.of(1998, 6, 21));
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Валидация даты рождения")
    void validateBirthDate() {
        User user = new User();
        user.setName("123");
        user.setEmail("example@gmail.com");
        user.setBirthday(LocalDate.of(2444, 6, 21));
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Валидация всех полей")
    void validateAllFields() {
        User user = new User();
        user.setName("123");
        user.setEmail("example@gmail.com");
        user.setBirthday(LocalDate.of(2002, 6, 21));
        user.setLogin("123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Валидация id")
    void validateId() {
        User user = new User();
        user.setId(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user, Update.class);

        assertFalse(violations.isEmpty());
    }
}