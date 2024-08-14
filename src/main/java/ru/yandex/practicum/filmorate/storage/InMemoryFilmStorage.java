package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long counter = 0L;

    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    public Film create(Film film) {
        counter++;
        film.setId(counter);
        films.put(film.getId(), film);

        return film;
    }

    public Film update(Film film) {
        Film oldFilm = films.get(film.getId());

        if (oldFilm == null) {
            log.warn("Film with id: {} does not exist", film.getId());
            throw new NotFoundException("Film not found.");
        }

        oldFilm.setDescription(film.getDescription());
        oldFilm.setName(film.getName());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());

        return oldFilm;
    }

    public void delete(Film film) {
        films.remove(film.getId());
    }

    public Film getFilm(Long id) {
        return films.get(id);
    }
}
