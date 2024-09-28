package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "filmId")
public class GenreRelation {
    private Long filmId;
    private Long genreId;
}
