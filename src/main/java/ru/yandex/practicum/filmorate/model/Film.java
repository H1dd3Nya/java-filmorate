package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.Update;

import java.time.LocalDate;

@Data
public class Film {
    private static final LocalDate FILM_BIRTHDAY = LocalDate.of(1895, 12, 28);
    @NotNull(groups = {Update.class})
    private Long id;
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Integer duration;

    @AssertFalse(message = "Release date is invalid")
    public boolean isValidReleaseDate() {
        return releaseDate.isBefore(FILM_BIRTHDAY);
    }
}
