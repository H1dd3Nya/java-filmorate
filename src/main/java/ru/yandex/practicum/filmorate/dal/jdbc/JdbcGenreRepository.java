package ru.yandex.practicum.filmorate.dal.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("JdbcGenreRepository")
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public Optional<Genre> getById(Long id) {
        String sql = "select * from \"genres\" where \"id\" = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return Optional.ofNullable(jdbc.query(sql, params, (ResultSet rs) -> {
            Genre genre = null;
            while (rs.next()) {
                genre = new Genre();
                genre.setId(rs.getLong("id"));
                genre.setName(rs.getString("name"));
            }

            return genre;
        }));
    }

    public List<Genre> getAll() {
        String sql = "select * from \"genres\"";
        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbc.query(sql, params, (ResultSet rs) -> {
            List<Genre> genres = new ArrayList<>();
            while (rs.next()) {
                Genre genre = new Genre();
                genre.setId(rs.getLong("id"));
                genre.setName(rs.getString("name"));
                genres.add(genre);
            }
            return genres;
        });
    }
}
