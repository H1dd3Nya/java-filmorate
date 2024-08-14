package ru.yandex.practicum.filmorate.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private final UserService userService = new UserService(new InMemoryUserStorage());
    private final FilmService filmService = new FilmService(new InMemoryFilmStorage(), userService);

    public static void assertEqualsFilms(Film expected, Film actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getReleaseDate(), actual.getReleaseDate());
        assertEquals(expected.getDuration(), actual.getDuration());
    }

    @Test
    @DisplayName("Добавление фильма")
    public void create_ShouldAddNewFilm() {
        Film film = new Film();
        film.setName("Test1");
        film.setDescription("Test1");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        filmService.create(film);

        assertEqualsFilms(film, filmService.get(1L));
    }

    @Test
    @DisplayName("Обновление фильма")
    public void update_ShouldUpdateFilm() {
        Film film = new Film();
        film.setName("Test1");
        film.setDescription("Test1");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film = filmService.create(film);

        film.setName("Test2");
        film.setDescription("Test2");
        film.setDuration(160);
        filmService.update(film);

        assertEqualsFilms(film, filmService.get(1L));
    }

    @Test
    @DisplayName("Получение фильма")
    public void get_ShouldGetFilm() {
        Film film = new Film();
        film.setName("Test1");
        film.setDescription("Test1");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        filmService.create(film);

        Film filmFromService = filmService.get(1L);

        assertEqualsFilms(film, filmFromService);
    }

    @Test
    @DisplayName("Удаление фильма")
    public void delete_ShouldDeleteFilm() {
        Film film = new Film();
        film.setName("Test1");
        film.setDescription("Test1");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        filmService.create(film);

        filmService.delete(film);

        assertNull(filmService.get(1L));
    }

    @Test
    @DisplayName("Получение всех фильмов")
    public void getAll_ShouldReturnAllFilms() {
        Film film1 = new Film();
        film1.setName("Test1");
        film1.setDescription("Test1");
        film1.setReleaseDate(LocalDate.now());
        film1.setDuration(120);

        Film film2 = new Film();
        film2.setName("Test2");
        film2.setDescription("Test2");
        film2.setReleaseDate(LocalDate.now().plusDays(2));
        film2.setDuration(80);

        filmService.create(film1);
        filmService.create(film2);
        List<Film> filmsFromService = filmService.getAll();

        assertEquals(2, filmsFromService.size());
        assertEqualsFilms(filmsFromService.get(0), film1);
        assertEqualsFilms(filmsFromService.get(1), film2);
    }

    @Test
    @DisplayName("Добавление лайка")
    public void addLike_ShouldAddLikeToFilm() {
        Film film = new Film();
        film.setName("Test1");
        film.setDescription("Test1");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        User user = new User();
        user.setLogin("Test1");
        user.setEmail("test@gmail.com");
        user.setName("Test Testovich");
        user.setBirthday(LocalDate.of(1995, 3, 22));
        film = filmService.create(film);
        user = userService.create(user);

        filmService.addLike(film.getId(), user.getId());

        assertEquals(1, filmService.get(film.getId()).getLikes().size());
        assertTrue(filmService.get(film.getId()).getLikes().contains(user.getId()));
    }

    @Test
    @DisplayName("Удаление лайка")
    public void removeLike_ShouldRemoveLike() {
        Film film = new Film();
        film.setName("Test1");
        film.setDescription("Test1");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        User user = new User();
        user.setLogin("Test1");
        user.setEmail("test@gmail.com");
        user.setName("Test Testovich");
        user.setBirthday(LocalDate.of(1995, 3, 22));
        film = filmService.create(film);
        user = userService.create(user);
        filmService.addLike(film.getId(), user.getId());

        filmService.removeLike(film.getId(), user.getId());

        assertEquals(0, filmService.get(film.getId()).getLikes().size());
    }

    @Test
    @DisplayName("Получение списка попуярных фильмов")
    public void getPopularFilms_ShouldReturnListOfPopularFilms() {
        Film film1 = new Film();
        film1.setName("Test1");
        film1.setDescription("Test1");
        film1.setReleaseDate(LocalDate.now());
        film1.setDuration(120);
        Film film2 = new Film();
        film2.setName("Test2");
        film2.setDescription("Test2");
        film2.setReleaseDate(LocalDate.now().plusDays(2));
        film2.setDuration(80);
        User user1 = new User();
        user1.setLogin("Test1");
        user1.setEmail("test1@gmail.com");
        user1.setName("Test1 Testovich");
        user1.setBirthday(LocalDate.of(1995, 3, 22));
        User user2 = new User();
        user2.setLogin("Test2");
        user2.setEmail("test2@gmail.com");
        user2.setName("Test2 Testovik");
        user2.setBirthday(LocalDate.of(1997, 8, 11));
        userService.create(user1);
        userService.create(user2);
        filmService.create(film1);
        filmService.create(film2);

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user1.getId());

        assertEquals(2, filmService.getPopularFilms(2).size());
        assertEqualsFilms(film1, filmService.getPopularFilms(2).get(0));
        assertEqualsFilms(film2, filmService.getPopularFilms(2).get(1));
    }

}