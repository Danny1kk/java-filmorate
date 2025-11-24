## Схема базы данных

![Схема — сущности и связь SQL](docs/Схема — сущности и связь SQL.png)

## Примеры SQL-запросов для java-filmorate

### Получение всех фильмов
SELECT * FROM films;

### Получение пользователя по id
SELECT * FROM users WHERE user_id = 5;

### Добавление лайка фильму
INSERT INTO likes (film_id, user_id)
VALUES (10, 3);

### Топ N популярных фильмов
SELECT f.*, COUNT(l.user_id) AS likes
FROM films f
LEFT JOIN likes l ON f.film_id = l.film_id
GROUP BY f.film_id
ORDER BY likes DESC
LIMIT 10;

### Жанры фильма
SELECT g.*
FROM genres g
JOIN film_genres fg ON g.genre_id = fg.genre_id
WHERE fg.film_id = 10;

### Возрастной рейтинг фильма
SELECT m.*
FROM mpa_ratings m
JOIN films f ON f.mpa_rating_id = m.mpa_rating_id
WHERE f.film_id = 10;

### Добавление друга (неподтвержденная заявка)
INSERT INTO friendships (user_id, friend_id, status)
VALUES (1, 2, 'unconfirmed')
ON CONFLICT (user_id, friend_id) DO UPDATE SET status = EXCLUDED.status;

### Подтверждение дружбы
UPDATE friendships
SET status = 'confirmed'
WHERE user_id = 1 AND friend_id = 2;

### Общие друзья двух пользователей
SELECT u.*
FROM users u
JOIN friendships f1 ON u.user_id = f1.friend_id AND f1.status = 'confirmed'
JOIN friendships f2 ON u.user_id = f2.friend_id AND f2.status = 'confirmed'
WHERE f1.user_id = 1 AND f2.user_id = 2;