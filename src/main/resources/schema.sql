CREATE TABLE IF NOT EXISTS "users"
(
    "id"       integer PRIMARY KEY AUTO_INCREMENT,
    "email"    varchar UNIQUE ,
    "login"    varchar NOT NULL UNIQUE,
    "name"     varchar NOT NULL,
    "birthday" date
);

CREATE TABLE IF NOT EXISTS "friends"
(
    "id"   integer PRIMARY KEY AUTO_INCREMENT,
    "user_id"   integer,
    "friend_id" integer
);

CREATE TABLE IF NOT EXISTS "films"
(
    "id"           integer PRIMARY KEY AUTO_INCREMENT,
    "name"         varchar,
    "description"  varchar(200),
    "release_date" date,
    "duration"     integer,
    "mpa"       integer
    );

CREATE TABLE IF NOT EXISTS "film_likes"
(
    "id"   integer PRIMARY KEY AUTO_INCREMENT,
    "film_id"  integer,
    "user_id" integer
);

CREATE TABLE IF NOT EXISTS "film_genres"
(
    "id"   integer PRIMARY KEY AUTO_INCREMENT,
    "film_id"  integer,
    "genre_id" integer
);

CREATE TABLE IF NOT EXISTS "mpa"
(
    "id"   integer PRIMARY KEY AUTO_INCREMENT,
    "name" varchar
);

CREATE TABLE IF NOT EXISTS "genres"
(
    "id"   integer PRIMARY KEY AUTO_INCREMENT,
    "name" varchar
);

ALTER TABLE "friends"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;

ALTER TABLE "film_genres"
    ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id") ON DELETE CASCADE;

ALTER TABLE "film_genres"
    ADD FOREIGN KEY ("genre_id") REFERENCES "genres" ("id") ON DELETE CASCADE;

ALTER TABLE "films"
    ADD FOREIGN KEY ("mpa") REFERENCES "mpa" ("id") ON DELETE CASCADE;

ALTER TABLE "films"
    ADD FOREIGN KEY ("mpa") REFERENCES "mpa" ("id") ON DELETE CASCADE;

ALTER TABLE "film_likes"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;

ALTER TABLE "film_likes"
    ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id") ON DELETE CASCADE;