package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "userId")
public class Like {
    private Long filmId;
    private Long userId;
}
