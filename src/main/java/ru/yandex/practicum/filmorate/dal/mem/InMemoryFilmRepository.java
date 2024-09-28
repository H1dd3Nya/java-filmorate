package ru.yandex.practicum.filmorate.dal.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmRepository implements FilmRepository {
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

    public Optional<Film> get(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    public void addLike(long filmId, long userId) {
        Film film = films.get(filmId);
        film.getLikes().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = films.get(filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> getMostPopular(int count) {
        return films.values().stream()
                .sorted((Film f1, Film f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }
}
