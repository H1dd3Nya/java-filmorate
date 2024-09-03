package ru.yandex.practicum.filmorate.dal.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("JdbcMpaRepository")
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Mpa> getById(Long mpaId) {
        String sql = """
            SELECT *
            FROM "mpa"
            WHERE "id"=:id;""";
        MapSqlParameterSource params = new MapSqlParameterSource("id", mpaId);

        return Optional.ofNullable(jdbc.query(sql, params, (ResultSet rs) -> {
            Mpa mpa = null;
            while (rs.next()) {
                if (mpa == null) {
                    mpa = new Mpa();
                }
                mpa.setId(rs.getLong("id"));
                mpa.setName(rs.getString("name"));
            }
            return mpa;
        }));
    }

    @Override
    public List<Mpa> getAll() {
        String sql = "select * from \"mpa\"";
        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbc.query(sql, params, (ResultSet rs) -> {
            List<Mpa> mpas = new ArrayList<>();
            while (rs.next()) {
                Mpa mpa = new Mpa();
                mpa.setId(rs.getLong("id"));
                mpa.setName(rs.getString("name"));
                mpas.add(mpa);
            }

            return mpas;
        });
    }
}
