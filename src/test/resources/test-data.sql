INSERT INTO "users" ("email", "login", "name", "birthday")
VALUES ( 'email.test@yandex.ru', 'login',  'user', null);
--
-- INSERT INTO "films"("name", "description", "release_date", "duration", "mpa")
-- VALUES ( 'test1', 'test12345', null, 180, 1);
--
INSERT INTO "films" ("name", "description", "release_date", "duration", "mpa")
VALUES ( 'test name', 'test film', null, 180, 2);
--
INSERT INTO "film_genres"("film_id", "genre_id") VALUES ( 1, 2 ), (1, 3);
INSERT INTO "film_likes" ("film_id", "user_id") VALUES ( 1, 1 );

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