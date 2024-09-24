package ru.yandex.practicum.filmorate.dal.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository("JdbcFilmRepository")
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private static Film extractFilm(ResultSet rs) throws SQLException {
        Film film = null;

        while (rs.next()) {
            if (film == null) {
                film = getFilm(rs);
            }

            if (rs.getLong("likes") != 0) {
                film.getLikes().add(rs.getLong("likes"));
            }

            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("genre_name"));

            if (genre.getId() != null && genre.getName() != null) {
                film.getGenres().add(genre);
            }
        }

        return film;
    }

    @Override
    public Optional<Film> get(Long id) {
        String sql = """
            SELECT f."id" AS id,
                   f."name" AS name,
                   f."description" AS description,
                   f."release_date" AS release_date,
                   f."duration" AS duration,
                   rating."id" AS rating_id,
                   rating."name" AS rating_name,
                   fl."user_id" AS likes,
                   g."id" AS genre_id,
                   g."name" AS genre_name
            FROM "films" AS f
            LEFT OUTER JOIN "film_likes" AS fl ON f."id" = fl."film_id"
                LEFT OUTER JOIN "mpa" AS rating ON f."mpa_id" = rating."id"
            LEFT OUTER JOIN "film_genres" AS fg ON f."id" = fg."film_id"
            LEFT OUTER JOIN "genres" AS g ON fg."genre_id" = g."id"
            WHERE f."id"=:id;""";

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);

        return Optional.ofNullable(jdbc.query(sql, params, JdbcFilmRepository::extractFilm));
    }

    private static Map<Long, Film> extractFilms(ResultSet rs) throws SQLException {
        Map<Long, Film> filmsMap = new LinkedHashMap<>();

        while (rs.next()) {
            Long filmId = rs.getLong("id");

            if (filmsMap.get(filmId) == null) {
                Film film = getFilm(rs);
                filmsMap.put(filmId, film);
            }

            if (rs.getLong("likes") != 0) {
                filmsMap.get(filmId).getLikes().add(rs.getLong("likes"));
            }
        }

        return filmsMap;
    }

    @Override
    public List<Film> getAll() {
        String filmsWithLikesQuery = """
                SELECT f."id" AS id,
                       f."name" AS name,
                       f."description" AS description,
                       f."release_date" AS release_date,
                       f."duration" AS duration,
                       rating."id" AS rating_id,
                       rating."name" AS rating_name,
                       fl."user_id" AS likes
                FROM "films" AS f
                LEFT OUTER JOIN "mpa" AS rating ON f."mpa_id" = rating."id"
                LEFT OUTER JOIN "film_likes" AS fl ON f."id" = fl."film_id\"""";

        String filmGenresQuery = """
                SELECT fg."film_id",
                       g."id" AS genre_id,
                       g."name" AS genre_name
                FROM "film_genres" AS fg
                LEFT OUTER JOIN "genres" as g ON fg."genre_id"=g."id"
                """;

        Map<Long, Film> films = jdbc.query(filmsWithLikesQuery, JdbcFilmRepository::extractFilms);

        return jdbc.query(filmGenresQuery, (ResultSet rs) -> {
            Set<Film> allFilms = new LinkedHashSet<>();
            while (rs.next()) {

                Genre genre = new Genre();
                genre.setId(rs.getLong("genre_id"));
                genre.setName(rs.getString("genre_name"));

                Film film = films.get(rs.getLong("film_id"));
                film.getGenres().add(genre);

                allFilms.add(film);
            }

            return new ArrayList<>(allFilms);
        });
    }

    @Override
    public void delete(Film film) {
        String sql = "DELETE FROM \"films\" WHERE \"films\".\"id\"= :id;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", film.getId());
        jdbc.update(sql,params);
    }

    @Override
    public Film create(Film film) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String filmUpdateQuery = """
                INSERT INTO "films"("name", "description", "release_date", "duration", "mpa_id")
                VALUES ( :name, :description, :releaseDate, :duration, :mpaId );""";
        String insertGenres = "INSERT INTO \"film_genres\" (\"film_id\", \"genre_id\")\n" +
                "    VALUES (:filmId, :genreId);";
        String insertLikes = "INSERT INTO \"film_likes\"(\"film_id\", \"user_id\")\n" +
                "VALUES (:filmId, :userId);";

        MapSqlParameterSource params = getParams(film);

        jdbc.update(filmUpdateQuery, params, keyHolder);
        film.setId(Long.valueOf(keyHolder.getKeyAs(Integer.class)));

        List<GenreRelation> genreRelations = new ArrayList<>();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                GenreRelation genreRelation = new GenreRelation();
                genreRelation.setFilmId(film.getId());
                genreRelation.setGenreId(genre.getId());
                genreRelations.add(genreRelation);
            }
            jdbc.batchUpdate(insertGenres, SqlParameterSourceUtils.createBatch(genreRelations), keyHolder);
        }

        List<Like> likes = new ArrayList<>();
        if (film.getLikes() != null) {
            for (Long userId : film.getLikes()) {
                Like like = new Like();
                like.setUserId(userId);
                like.setFilmId(film.getId());
                likes.add(like);
            }
            jdbc.batchUpdate(insertLikes, SqlParameterSourceUtils.createBatch(likes), keyHolder);
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        String filmUpdateQuery = """
                UPDATE "films" SET "name" = :name, "description" = :description, "release_date" = :releaseDate,
                    "duration" = :duration, "mpa_id" = :mpaId WHERE "id"= :filmId;""";
        String insertGenres = """
                INSERT INTO "film_genres" ("film_id", "genre_id")
                VALUES ( :filmId, :genreId );""";
        String insertLikes = "INSERT INTO \"film_likes\" (\"film_id\", \"user_id\") VALUES ( :filmId, :userId );";

        String deleteGenres = "DELETE FROM \"film_genres\" WHERE \"film_id\" = :filmId;";
        String deleteLikes = "DELETE FROM \"film_likes\" WHERE \"film_id\" = :filmId;";

        MapSqlParameterSource params = getParams(film);

        jdbc.update(filmUpdateQuery, params);

        jdbc.update(deleteGenres, params);
        jdbc.update(deleteLikes, params);

        List<GenreRelation> genreRelations = new ArrayList<>();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                GenreRelation genreRelation = new GenreRelation();
                genreRelation.setFilmId(film.getId());
                genreRelation.setGenreId(genre.getId());
                genreRelations.add(genreRelation);
            }
            jdbc.batchUpdate(insertGenres, SqlParameterSourceUtils.createBatch(genreRelations));
        }

        List<Like> likes = new ArrayList<>();
        if (film.getLikes() != null) {
            for (Long userId : film.getLikes()) {
                Like like = new Like();
                like.setUserId(userId);
                like.setFilmId(film.getId());
                likes.add(like);
            }
            jdbc.batchUpdate(insertLikes, SqlParameterSourceUtils.createBatch(likes));
        }

        return get(film.getId()).orElseThrow(() -> new NotFoundException("Film not found"));
    }

    @Override
    public List<Film> getMostPopular(int count) {
        String sql = """
            SELECT f."id" AS id,
                   f."name" AS name,
                   f."description" AS description,
                   f."release_date" AS release_date,
                   f."duration" AS duration,
                   rating."id" AS rating_id,
                   rating."name" AS rating_name,
                   fl."user_id" AS likes,
                   g."id" ,
                   g."name"
            FROM (SELECT "film_id"
                  FROM "film_likes"
                  GROUP BY "film_id"
                  ORDER BY COUNT(*) DESC
                  LIMIT (:count)) AS best_rating
            LEFT OUTER JOIN "films" AS f ON best_rating."film_id"=f."id"
            LEFT OUTER JOIN "film_likes" AS fl ON f."id" = fl."film_id"
                LEFT OUTER JOIN "mpa" AS rating ON f."mpa_id" = rating."id"
            LEFT OUTER JOIN "film_genres" AS fg ON f."id" = fg."film_id"
            LEFT OUTER JOIN "genres" AS g ON fg."genre_id" = g."id";""";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("count", count);

        Map<Long, Film> films = jdbc.query(sql, params, JdbcFilmRepository::extractFilms);
        List<Film> popularFilms = new ArrayList<>();

        if (films != null) {
            for (Long filmId : films.keySet()) {
                popularFilms.add(get(filmId).orElseThrow(() -> new NotFoundException("Film not found")));
            }
            return popularFilms;
        }

        throw new NotFoundException("No films found");
    }

    @Override
    public void addLike(long filmId, long userId) {
        if (hasLike(filmId, userId)) {
            throw new IllegalArgumentException("Film already liked");
        }

        String sql = "INSERT INTO \"film_likes\"(\"film_id\", \"user_id\")  VALUES (:filmId, :userId);";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmId", filmId);
        params.addValue("userId", userId);

        jdbc.update(sql, params, keyHolder);
    }

    private static Film getFilm(ResultSet rs) throws SQLException {
        Film film = new Film();

        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));

        LocalDate releaseDate = null;
        if (rs.getDate("release_date") != null) {
            releaseDate = rs.getDate("release_date").toLocalDate();
        }
        film.setReleaseDate(releaseDate);

        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("rating_id"));
        mpa.setName(rs.getString("rating_name"));
        film.setMpa(mpa);

        film.setGenres(new LinkedHashSet<>());
        film.setLikes(new HashSet<>());

        return film;
    }

    private boolean hasLike(long filmId, long userId) {

        String sql = "SELECT * FROM \"film_likes\" WHERE \"film_id\" = :film_id AND \"user_id\" = :user_id;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);
        params.addValue("user_id", userId);

        List<Like> likesCount = jdbc.query(sql, params, (ResultSet rs) -> {
            List<Like> likes = new ArrayList<>();
            Like like = null;
            while (rs.next()) {
                if (like == null) {
                    like = new Like();
                }
                like.setFilmId(rs.getLong("film_id"));
                like.setUserId(rs.getLong("user_id"));
                likes.add(like);
            }

            return likes;
        });

        return likesCount != null && !likesCount.isEmpty();
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sql = "DELETE FROM \"film_likes\" WHERE \"film_id\" = :filmId AND \"user_id\" = :userId;";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmId", filmId);
        params.addValue("userId", userId);

        jdbc.update(sql, params, keyHolder);
    }

    private MapSqlParameterSource getParams(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (film.getId() != null) {
            params.addValue("filmId", film.getId());
        }

        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("releaseDate", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpaId", film.getMpa().getId());

        return params;
    }
}
