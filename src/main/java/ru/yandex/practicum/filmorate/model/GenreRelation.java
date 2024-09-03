package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class GenreRelation {
    private Long filmId;
    private Long genreId;
}
