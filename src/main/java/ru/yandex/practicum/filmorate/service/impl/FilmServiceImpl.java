package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    public FilmServiceImpl(@Qualifier("JdbcFilmRepository") FilmRepository filmRepository,
                           @Qualifier("JdbcUserRepository") UserRepository userRepository,
                           @Qualifier("JdbcGenreRepository")GenreRepository genreRepository,
                           @Qualifier("JdbcMpaRepository")MpaRepository mpaRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
    }

    @Override
    public List<Film> getAll() {
        return filmRepository.getAll();
    }

    @Override
    public Film get(Long filmId) {
        return filmRepository.get(filmId).orElseThrow(() -> new NotFoundException("Film not found"));
    }

    @Override
    public Film create(Film film) {
        mpaRepository.getById(film.getMpa()
                .getId())
                .orElseThrow(() -> new IllegalArgumentException("Mpa not found"));

        isGenreExist(film);

//        if (film.getLikes() == null) {
//            film.setLikes(new LinkedHashSet<>());
//        }
//
//        if (film.getGenres() == null) {
//            film.setGenres(new LinkedHashSet<>());
//        }

        return filmRepository.create(film);
    }

    @Override
    public Film update(Film film) {
        Film f = filmRepository.get(film.getId()).orElseThrow(() -> new NotFoundException("Film not found"));

        Mpa mpa = mpaRepository.getById(f.getMpa()
                .getId())
                .orElseThrow(() -> new IllegalArgumentException("Mpa not found"));

        isGenreExist(f);

        f.setName(film.getName());
        f.setDescription(film.getDescription());
        f.setReleaseDate(film.getReleaseDate());
        f.setDuration(film.getDuration());
        f.setMpa(mpa);

        if (film.getLikes() == null) {
            film.setLikes(new LinkedHashSet<>());
        }

        if (film.getGenres() == null) {
            film.setGenres(new LinkedHashSet<>());
        }

        return filmRepository.update(film);
    }

    @Override
    public void delete(Film film) {
        filmRepository.delete(film);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        filmRepository.get(filmId).orElseThrow(() -> new NotFoundException("Film not found"));

        if (!isUserExist(userId)) {
            throw new NotFoundException("User not found");
        }

        filmRepository.addLike(filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        filmRepository.get(filmId).orElseThrow(() -> new NotFoundException("Film not found"));

        if (!isUserExist(userId)) {
            throw new NotFoundException("User not found");
        }

        filmRepository.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmRepository.getMostPopular(count);
    }

    private boolean isUserExist(Long id) {
        return userRepository.get(id).isPresent();
    }

    private void isGenreExist(Film film) {
        List<Long> genreIds = null;
        if (film.getGenres() != null) {
            genreIds = film.getGenres().stream().map(Genre::getId).toList();
        }

        if (genreIds != null) {
            for (Long id : genreIds) {
                genreRepository.getById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Genre not found"));
            }
        }
    }
}
