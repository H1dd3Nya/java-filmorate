package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    Optional<Genre> getById(Long id);
    List<Genre> getAll();
}
