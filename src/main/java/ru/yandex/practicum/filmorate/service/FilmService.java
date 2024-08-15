package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getAll();

    Film get(Long filmId);

    Film create(Film film);

    Film update(Film film);

    void delete(Film film);

    Film addLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);
}
