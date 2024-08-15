package ru.yandex.practicum.filmorate.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceImplTest {

    private final UserStorage userStorage = new InMemoryUserStorage();
    private final FilmService filmServiceImpl = new FilmServiceImpl(new InMemoryFilmStorage(),
            userStorage);

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

        filmServiceImpl.create(film);

        assertEqualsFilms(film, filmServiceImpl.get(1L));
    }

    @Test
    @DisplayName("Обновление фильма")
    public void update_ShouldUpdateFilm() {
        Film film = new Film();
        film.setName("Test1");
        film.setDescription("Test1");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film = filmServiceImpl.create(film);

        film.setName("Test2");
        film.setDescription("Test2");
        film.setDuration(160);
        filmServiceImpl.update(film);

        assertEqualsFilms(film, filmServiceImpl.get(1L));
    }

    @Test
    @DisplayName("Получение фильма")
    public void get_ShouldGetFilm() {
        Film film = new Film();
        film.setName("Test1");
        film.setDescription("Test1");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        filmServiceImpl.create(film);

        Film filmFromService = filmServiceImpl.get(1L);

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
        filmServiceImpl.create(film);

        filmServiceImpl.delete(film);

        assertThrows(NotFoundException.class, () -> filmServiceImpl.get(1L));
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

        filmServiceImpl.create(film1);
        filmServiceImpl.create(film2);
        List<Film> filmsFromService = filmServiceImpl.getAll();

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
        film = filmServiceImpl.create(film);
        user = userStorage.create(user);

        filmServiceImpl.addLike(film.getId(), user.getId());

        assertEquals(1, filmServiceImpl.get(film.getId()).getLikes().size());
        assertTrue(filmServiceImpl.get(film.getId()).getLikes().contains(user.getId()));
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
        film = filmServiceImpl.create(film);
        user = userStorage.create(user);
        filmServiceImpl.addLike(film.getId(), user.getId());

        filmServiceImpl.removeLike(film.getId(), user.getId());

        assertEquals(0, filmServiceImpl.get(film.getId()).getLikes().size());
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
        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);
        filmServiceImpl.create(film1);
        filmServiceImpl.create(film2);

        filmServiceImpl.addLike(film1.getId(), 1L);
        filmServiceImpl.addLike(film1.getId(), 2L);
        filmServiceImpl.addLike(film2.getId(), 1L);

        assertEquals(2, filmServiceImpl.getPopularFilms(2).size());
        assertEqualsFilms(film1, filmServiceImpl.getPopularFilms(2).get(0));
        assertEqualsFilms(film2, filmServiceImpl.getPopularFilms(2).get(1));
    }

}