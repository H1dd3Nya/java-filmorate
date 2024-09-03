package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;
import ru.yandex.practicum.filmorate.annotation.Update;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
@ToString
public class Film {
    @NotNull(groups = {Update.class})
    private Long id;
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private Set<Long> likes;
    private LinkedHashSet<Genre> genres;
    private Mpa mpa;
}
