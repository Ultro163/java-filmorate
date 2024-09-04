DROP TABLE IF EXISTS reviews_users_likes;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS genre_film;
DROP TABLE IF EXISTS film_likes_users;
DROP TABLE IF EXISTS films_director;
DROP TABLE IF EXISTS directors;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS friendship;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS rating_mpa;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS history_actions;


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
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews(
    review_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
    content     VARCHAR(200),
    is_positive BOOLEAN,
    user_id     BIGINT REFERENCES users (user_id),
    film_id     BIGINT REFERENCES films (film_id),
    useful      INTEGER
);

CREATE TABLE IF NOT EXISTS reviews_users_likes(
    review_id BIGINT REFERENCES REVIEWS (REVIEW_ID) ON DELETE CASCADE ON UPDATE CASCADE,
    user_id BIGINT REFERENCES USERS (USER_ID) ON DELETE CASCADE ON UPDATE CASCADE,
    like_or_dislike ENUM ('like', 'dislike'),
    CONSTRAINT PK_REVIEWS_USERS_LIKES PRIMARY KEY (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS history_actions (
    event_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    time_action BIGINT,
    type VARCHAR(20),
    operation VARCHAR(20),
    entity_id BIGINT,
    CONSTRAINT HISTORY_PK PRIMARY KEY (event_id)
);

CREATE TABLE IF NOT EXISTS directors (
    director_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name varchar
);

CREATE TABLE IF NOT EXISTS films_director (
    film_id BIGINT REFERENCES films(film_id) ON DELETE CASCADE ,
    director_id BIGINT REFERENCES directors(director_id) ON DELETE CASCADE ,
    CONSTRAINT films_director_unique UNIQUE (film_id,director_id)
);