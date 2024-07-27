package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    long counter = 0L;

    @GetMapping
    public List<Film> getAll() {
        log.info("Started collecting films");
        return films.values().stream().toList();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        log.info("Started creating new film");
        counter++;
        film.setId(counter);
        films.put(film.getId(), film);

        log.info("Film successfully created");
        return film;
    }

    @PutMapping
    public Film update(@Validated(Update.class) @RequestBody Film film) {
        log.info("Started updating film");
        Film oldFilm = films.get(film.getId());

        if (oldFilm == null) {
            log.warn("Film with id: {} does not exist", film.getId());
            throw new NotFoundException("Film not found.");
        }

        log.info("Updating film info");
        oldFilm.setDescription(film.getDescription());
        oldFilm.setName(film.getName());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());
        log.info("Updated film fields with name: {}, description: {}, releaseDate: {}, duration: {}",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());

        log.info("Film successfully updated");
        return oldFilm;
    }
}
