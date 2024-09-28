package ru.yandex.practicum.filmorate.dal.jdbc;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import(JdbcMpaRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcMpaRepository")
class JdbcMpaRepositoryTest {
    private static final Long TEST_MPA_ID = 1L;
    private final JdbcMpaRepository mpaRepository;

    static Mpa getTestMpa() {
        Mpa mpa = new Mpa();
        mpa.setId(TEST_MPA_ID);
        mpa.setName("G");
        return mpa;
    }

    @Test
    @DisplayName("Получение рейтинга по id")
    public void getById_shouldReturnMpa() {
        Optional<Mpa> mpaOptional = mpaRepository.getById(TEST_MPA_ID);

        assertThat(mpaOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestMpa());
    }

    @Test
    @DisplayName("Получение всех рейтингов")
    public void getAll_shouldReturnAllRatings() {
        List<Mpa> ratings = mpaRepository.getAll();

        assertEquals(5, ratings.size());
        assertThat(ratings.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(getTestMpa());
    }
}