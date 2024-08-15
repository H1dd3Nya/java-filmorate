package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film get(Long filmId) {
        return filmStorage.get(filmId).orElseThrow(() -> new NotFoundException("Film not found"));
    }

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    @Override
    public void delete(Film film) {
        filmStorage.delete(film);
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId).orElseThrow(() -> new NotFoundException("Film not found"));

        if (!isUserExist(userId)) {
            throw new NotFoundException("User not found");
        }

        film.getLikes().add(userId);
        filmStorage.update(film);

        return film;
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId).orElseThrow(() -> new NotFoundException("Film not found"));

        if (!isUserExist(userId)) {
            throw new NotFoundException("User not found");
        }

        film.getLikes().remove(userId);
        filmStorage.update(film);

        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((Film f1, Film f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean isUserExist(Long id) {
        return userStorage.get(id).isPresent();
    }
}
