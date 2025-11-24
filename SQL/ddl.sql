-- Справочники
CREATE TABLE mpa_ratings (
    mpa_rating_id SERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE genres (
    genre_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Пользователи
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(100) NOT NULL,
    name VARCHAR(255),
    birthday DATE
);

-- Дружба (запрос/подтверждение)
CREATE TABLE friendships (
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    friend_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('unconfirmed', 'confirmed')),
    created_at TIMESTAMP DEFAULT now(),
    PRIMARY KEY (user_id, friend_id)
);

-- Фильмы
CREATE TABLE films (
    film_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE,
    duration INT NOT NULL,
    mpa_rating_id INT REFERENCES mpa_ratings(mpa_rating_id)
);

-- Связь фильм-жанр
CREATE TABLE film_genres (
    film_id INT NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    genre_id INT NOT NULL REFERENCES genres(genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

-- Лайки
CREATE TABLE likes (
    film_id INT NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT now(),
    PRIMARY KEY (film_id, user_id)
);