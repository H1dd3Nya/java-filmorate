package ru.yandex.practicum.filmorate.dal.jdbc;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@Import(JdbcFilmRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcFilmRepository")
class JdbcFilmRepositoryTest {
    private static final Long TEST_FILM_ID = 1L;
    private final JdbcFilmRepository filmRepository;

    static Film getTestFilm() {
        Film film = new Film();
        film.setId(TEST_FILM_ID);
        film.setName("test name");
        film.setDescription("test film");
        film.setDuration(180);
        film.setReleaseDate(null);

        Mpa mpa = new Mpa();
        mpa.setId(2L);
        mpa.setName("PG");
        film.setMpa(mpa);

        film.setLikes(new HashSet<>());

        film.setGenres(new LinkedHashSet<>());
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Комедия");
        film.getGenres().add(genre);
        genre = new Genre();
        genre.setId(2L);
        genre.setName("Драма");
        film.getGenres().add(genre);

        return film;
    }

    @Test
    @DisplayName("Получение фильма по id")
    public void get_shouldReturnFilmById() {
        Optional<Film> filmOptional = filmRepository.get(TEST_FILM_ID);

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm());
    }

    @Test
    @DisplayName("Получение всех фильмов")
    public void getAll_shouldReturnAllFilms() {
        List<Film> films = filmRepository.getAll();

        System.out.println(films);
        assertEquals(1, films.size());
        assertThat(films.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm());
    }

    @Test
    @DisplayName("Создание фильма")
    public void create_shouldCreateFilm() {
        Film film = new Film();
        film.setName("test2");
        film.setDescription("test2");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(1999, 1, 11));
        Mpa mpa = new Mpa();
        mpa.setId(2L);
        mpa.setName("PG");
        film.setMpa(mpa);

        film.setLikes(new HashSet<>());
        film.setGenres(new LinkedHashSet<>());

        Film filmCreated = filmRepository.create(film);
        assertThat(filmCreated)
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    @DisplayName("Обновление фильма")
    public void update_shouldUpdateFilm() {
        Film film = getTestFilm();
        film.setId(TEST_FILM_ID);
        film.setName("updated name");
        film.setReleaseDate(LocalDate.of(1998, 11, 24));

        filmRepository.update(film);
        Optional<Film> filmUpdated = filmRepository.get(film.getId());
        assertThat(filmUpdated)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("friends")
                .isEqualTo(film);
    }

    @Test
    @DisplayName("Удаление фильма")
    public void delete_shouldDeleteFilm() {
        Film film = getTestFilm();

        filmRepository.delete(film);
        List<Film> films = filmRepository.getAll();

        assertEquals(0, films.size());
    }

    @Test
    @DisplayName("Добавление лайка фильму")
    public void addLike_shouldAddLikeToFilm() {
        Film film = getTestFilm();
        filmRepository.addLike(film.getId(), 1L);

        Film filmFromRepo = filmRepository.get(TEST_FILM_ID).orElseThrow(() -> new NotFoundException("Film not found"));

        assertEquals(1, filmFromRepo.getLikes().size());
        assertTrue(filmFromRepo.getLikes().contains(1L));
    }

    @Test
    @DisplayName("Получение самых популярных фильмов")
    public void getPopularFilms_shouldReturnListOfPopularFilms() {
        Film film = getTestFilm();
        filmRepository.addLike(film.getId(), 1L);

        Film film2 = new Film();
        film2.setName("test2");
        film2.setDescription("test2");
        film2.setDuration(180);
        film2.setReleaseDate(LocalDate.of(1999, 1, 11));
        Mpa mpa = new Mpa();
        mpa.setId(2L);
        mpa.setName("PG");
        film2.setMpa(mpa);
        film2.setLikes(new HashSet<>());
        film2.setGenres(new LinkedHashSet<>());

        filmRepository.create(film2);
        filmRepository.addLike(film2.getId(), 1L);

        List<Film> films = filmRepository.getMostPopular(10);
        film = filmRepository.get(TEST_FILM_ID).orElseThrow(() -> new NotFoundException("Film not found"));

        assertEquals(2, films.size());
        assertThat(films.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    @DisplayName("Удаление лайка у фильма")
    public void removeLike_shouldRemoveLikeFromFilm() {
        Film film = getTestFilm();
        filmRepository.addLike(film.getId(), 1L);

        filmRepository.removeLike(film.getId(), 1L);
        Film filmFromRepo = filmRepository.get(TEST_FILM_ID).orElseThrow(() -> new NotFoundException("Film not found"));

        assertEquals(0, filmFromRepo.getLikes().size());
    }
}