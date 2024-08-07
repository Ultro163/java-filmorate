DROP TABLE IF EXISTS genre_film;
DROP TABLE IF EXISTS film_likes_users;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS friendship;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS rating_mpa;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email varchar UNIQUE NOT NULL,
    login varchar UNIQUE NOT NULL,
    name varchar,
    birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS friendship (
    user1_id BIGINT REFERENCES users(USER_ID) ON DELETE CASCADE,
    user2_id BIGINT REFERENCES users(USER_ID) ON DELETE CASCADE,
    status ENUM('unconfirmed', 'confirmed') DEFAULT 'unconfirmed',
    CONSTRAINT friendship UNIQUE(user1_id, user2_id, status)
);

CREATE TABLE IF NOT EXISTS rating_mpa (
    rating_id integer PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(15) UNIQUE
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name TEXT NOT NULL ,
    description VARCHAR(200),
    release_date DATE,
    duration integer,
    rating_id integer REFERENCES rating_mpa(rating_id)
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id integer PRIMARY KEY AUTO_INCREMENT,
    genre_name TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS genre_film (
    film_id BIGINT REFERENCES films(film_id) ON DELETE CASCADE,
    genre_id integer REFERENCES genres(genre_id),
    CONSTRAINT genre_film UNIQUE (film_id,genre_id)
);

CREATE TABLE IF NOT EXISTS film_likes_users (
    film_id BIGINT REFERENCES films(film_id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT unique_likes UNIQUE (film_id, user_id)
);