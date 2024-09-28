INSERT INTO "genres" ("name")
VALUES ( 'Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

INSERT INTO "mpa" ("name")
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

INSERT INTO "users" ("email", "login", "name", "birthday")
VALUES ('email.test@yandex.ru', 'login', 'user', null);

INSERT INTO "films" ("name", "description", "release_date", "duration", "mpa_id")
VALUES ('test name', 'test film', null, 180, 2);

INSERT INTO "film_genres"("film_id", "genre_id")
VALUES (1, 1),
       (1, 2);