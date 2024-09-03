package ru.yandex.practicum.filmorate.dal.jdbc;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(JdbcGenreRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcGenreRepository")
class JdbcGenreRepositoryTest {
    private static final Long TEST_GENRE_ID = 1L;
    private final JdbcGenreRepository genreRepository;

    static Genre getTestGenre() {
        Genre genre = new Genre();
        genre.setId(TEST_GENRE_ID);
        genre.setName("Комедия");
        return genre;
    }

    @Test
    @DisplayName("Получение жанра по id")
    public void getById_shouldReturnGenre() {
        Optional<Genre> genreOptional = genreRepository.getById(TEST_GENRE_ID);

        assertThat(genreOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestGenre());
    }

    @Test
    @DisplayName("Получение всех жанров")
    public void getAll_shouldReturnAllGenres() {
        List<Genre> genres = genreRepository.getAll();

        assertEquals(6, genres.size());
        assertThat(genres.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(getTestGenre());
    }
}