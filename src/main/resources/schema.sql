DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS rating CASCADE;

CREATE TABLE IF NOT EXISTS rating (
    rating_id INT PRIMARY KEY,
    mpa_rating VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(50) NOT NULL,
    name VARCHAR(255),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    rating_id INTEGER REFERENCES rating(rating_id),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date DATE,
    duration INTEGER
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id INT PRIMARY KEY,
    genre_name VARCHAR
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id INTEGER NOT NULL REFERENCES films(film_id),
    genre_id INTEGER NOT NULL REFERENCES genres(genre_id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
    user_id INTEGER REFERENCES users(user_id),
    film_id INTEGER REFERENCES films(film_id),
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS friends (
    user_id INTEGER REFERENCES users(user_id),
    friend_id INTEGER REFERENCES users(user_id),
    friendship_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    friendship_status VARCHAR
);

MERGE INTO rating (rating_id, mpa_rating) KEY (rating_id) VALUES
                                                              (1, 'G'),
                                                              (2, 'PG'),
                                                              (3, 'PG-13'),
                                                              (4, 'R'),
                                                              (5, 'NC-17');

MERGE INTO genres (genre_id, genre_name) KEY (genre_id) VALUES
                                                            (1, 'Комедия'),
                                                            (2, 'Драма'),
                                                            (3, 'Мультфильм'),
                                                            (4, 'Триллер'),
                                                            (5, 'Документальный'),
                                                            (6, 'Боевик');