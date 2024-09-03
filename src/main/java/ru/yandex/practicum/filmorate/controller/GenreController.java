package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreController(@Qualifier("JdbcGenreRepository") GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @GetMapping
    public List<Genre> getAll() {
        return genreRepository.getAll();
    }

    @GetMapping("/{id}")
    public Genre get(@PathVariable Long id) {
        return genreRepository.getById(id).orElseThrow(() -> new NotFoundException("Genre not found"));
    }
}
