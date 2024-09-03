package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class GenreRelation {
    private Long film_id;
    private Long genre_id;
}
