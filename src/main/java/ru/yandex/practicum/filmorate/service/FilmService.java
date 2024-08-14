package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService implements BaseFilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film get(Long filmId) {
        return filmStorage.getFilm(filmId);
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
        Film film = filmStorage.getFilm(filmId);

        if (film == null) {
            log.warn("Film with id={} not found, stop adding", filmId);
            throw new NotFoundException("Film not found");
        }

        if (userService.get(userId) == null) {
            throw new NotFoundException("User is unknown");
        }

        film.getLikes().add(userId);
        filmStorage.update(film);

        return film;
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);

        if (film == null) {
            log.warn("Film with id={} not found, stop removing", filmId);
            throw new NotFoundException("Film not found");
        }

        if (userService.get(userId) == null) {
            throw new NotFoundException("User is unknown");
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

}
