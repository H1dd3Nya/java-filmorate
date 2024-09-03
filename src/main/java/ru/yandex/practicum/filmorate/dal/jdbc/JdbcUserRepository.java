package ru.yandex.practicum.filmorate.dal.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository("JdbcUserRepository")
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<User> getAll() {
        String sql = """
            SELECT u."id" AS user_id,
                   u."email" AS user_email,
                   u."login" AS user_login,
                   u."name" AS user_name,
                   u."birthday" AS user_birthday,
                   f."friend_id" AS friend_id
            FROM "users" AS u
            LEFT OUTER JOIN "friends" AS f ON u."id" = f."user_id";""";
        return jdbc.query(sql, (ResultSet rs) -> {
            Set<User> users = new LinkedHashSet<>();
            while (rs.next()) {
                User user = getUser(rs);
                user.getFriends().add(rs.getLong("friend_id"));
                users.add(user);
            }

            return new ArrayList<>(users);
        });
    }

    @Override
    public User create(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String userCreateQuery = """
                  INSERT INTO "users"("email", "login", "name", "birthday")
                  VALUES ( :email, :login, :name,:birthday );""";
        String insertFriends = "INSERT INTO \"friends\"(\"user_id\", \"friend_id\") VALUES ( :user_id, :friend_id );";

        MapSqlParameterSource params = getParams(user);

        jdbc.update(userCreateQuery, params, keyHolder);

        jdbc.batchUpdate(insertFriends, SqlParameterSourceUtils.createBatch(user.getFriends()));

        user.setId(Long.valueOf(keyHolder.getKeyAs(Integer.class)));
        return user;
    }

    @Override
    public User update(User user) {
        String userCreateQuery = """
                UPDATE "users"
                SET "email"= :email, "login"= :login, "name"= :name, "birthday"= :birthday
                WHERE "id"= :id;""";
        String insertFriends = "INSERT INTO \"friends\"(\"user_id\", \"friend_id\") VALUES ( :user_id, :friend_id );";
        String deleteFriends = "DELETE FROM \"friends\" WHERE \"user_id\"= :user_id;";

        MapSqlParameterSource params = getParams(user);
        params.addValue("user_id", user.getId());

        jdbc.update(userCreateQuery, params);

        jdbc.update(deleteFriends, params);

        jdbc.batchUpdate(insertFriends, SqlParameterSourceUtils.createBatch(user.getFriends()));

        return user;
    }

    @Override
    public void delete(User user) {
        String sql = "DELETE FROM \"users\" WHERE \"users\".\"id\"= :id;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", user.getId());
        jdbc.update(sql,params);
    }

    @Override
    public Optional<User> get(Long id) {
        String sql = """
            SELECT u."id" AS user_id,
                   u."email" AS user_email,
                   u."login" AS user_login,
                   u."name" AS user_name,
                   u."birthday" AS user_birthday,
                   f."friend_id" AS friend_id
            FROM "users" AS u
            LEFT OUTER JOIN "friends" AS f ON u."id" = f."user_id"
            WHERE u."id" = :id;""";

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);

        return Optional.ofNullable(jdbc.query(sql, params, JdbcUserRepository::extractUser));
    }

    @Override
    public void addFriend(Long user, Long target) {
        String sql = "INSERT INTO \"friends\"(\"user_id\", \"friend_id\") VALUES (:target, :userId);";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("target", target);
        params.addValue("userId", user);

        jdbc.update(sql, params);
    }

    @Override
    public void removeFriend(Long user, Long target) {
        String sql = "DELETE FROM \"friends\" WHERE \"user_id\" = :userId AND \"friend_id\" = :target;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("target", target);
        params.addValue("userId", user);

        jdbc.update(sql, params);
    }

    @Override
    public Set<User> getFriends(Long id) {
        String sql = """
            SELECT *
            FROM "users"
            WHERE "id" IN (SELECT "friend_id"
                           FROM "friends"
                           WHERE "user_id"=:id);""";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        return jdbc.query(sql, params, (ResultSet rs) -> {
            Set<User> friends = new HashSet<>();
            while (rs.next()) {
                User user = new User();

                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setLogin(rs.getString("login"));

                Date birthday = rs.getDate("birthday");
                if (birthday != null) {
                    user.setBirthday(birthday.toLocalDate());
                }

                user.setBirthday(null);

                friends.add(user);
            }

            if (friends.isEmpty()) {
                return new HashSet<>();
            }

            return friends;
        });
    }

    @Override
    public Set<User> getCommonFriends(Long user, Long target) {
        String sql = """
                SELECT "friend_id"
                FROM "friends"
                WHERE "user_id" = :user1_id OR "user_id" = :user2_id
                GROUP BY "friend_id"
                HAVING COUNT(*) > 1;""";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user1_id", user);
        params.addValue("user2_id", target);
        List<Long> friendIds = jdbc.queryForList(sql, params, Long.class);
        Set<User> friends = new LinkedHashSet<>();

        for (Long friendId : friendIds) {
            friends.add(get(friendId).get());
        }

        return friends;
    }

    private static User getUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("user_email"));
        user.setLogin(rs.getString("user_login"));
        user.setName(rs.getString("user_name"));
        user.setBirthday(null);

        LocalDate birthday = null;
        if (rs.getDate("birthday") != null) {
            birthday = rs.getDate("birthday").toLocalDate();
        }
        user.setBirthday(birthday);

        return user;
    }

    private MapSqlParameterSource getParams(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (user.getId() != null) {
            params.addValue("id", user.getId());
        }

        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());
        return params;
    }

    private static User extractUser(ResultSet rs) throws SQLException {
        User user = null;

        while (rs.next()) {
            if (user == null) {
                user = getUser(rs);
            }

            if (rs.getLong("friend_id") != 0) {
                user.getFriends().add(rs.getLong("friend_id"));
            }
        }
        return user;
    }
}
