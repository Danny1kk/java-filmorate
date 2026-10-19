# java-filmorate

Шаблонный репозиторий для бэкенд-приложения Filmorate (сервис для работы с фильмами и оценками пользователей).

## Схема базы данных
![ER-diagram](docs/Schema_entities_and_SQL_relationships.png)

---

### Описание таблиц
- `users` — таблица пользователей
- `friendships` — таблица друзей и статусов дружбы
- `films` — таблица фильмов
- `mpa_ratings` — справочник возрастных рейтингов по версии MPA
- `genres` — справочник жанров
- `film_genres` — таблица связи фильмов и их жанров
- `likes` — таблица лайков пользователей фильмам

---

### Примеры SQL-запросов

#### Получение всех фильмов
```sql
SELECT * 
FROM films;
```

#### Получение пользователя по ID
```sql
SELECT * 
FROM users 
WHERE user_id = 5;
```

#### Топ N популярных фильмов
```sql
SELECT f.*, COUNT(l.user_id) AS likes
FROM films f
LEFT JOIN likes l ON f.film_id = l.film_id
GROUP BY f.film_id
ORDER BY likes DESC
LIMIT 10;
```

#### Жанры фильма
```sql
SELECT g.*
FROM genres g
JOIN film_genres fg ON g.genre_id = fg.genre_id
WHERE fg.film_id = 10;
```

#### Возрастной рейтинг фильма
```sql
SELECT m.*
FROM mpa_ratings m
JOIN films f ON f.mpa_rating_id = m.mpa_rating_id
WHERE f.film_id = 10;
```

#### Добавление друга (неподтвержденная заявка)
```sql
INSERT INTO friendships (user_id, friend_id, status)
VALUES (1, 2, 'unconfirmed')
ON CONFLICT (user_id, friend_id) DO UPDATE SET status = EXCLUDED.status;
```

#### Подтверждение дружбы
```sql
UPDATE friendships
SET status = 'confirmed'
WHERE user_id = 1 AND friend_id = 2;
```

#### Общие друзья двух пользователей
```sql
SELECT u.*
FROM users u
JOIN friendships f1 ON u.user_id = f1.friend_id AND f1.status = 'confirmed'
JOIN friendships f2 ON u.user_id = f2.friend_id AND f2.status = 'confirmed'
WHERE f1.user_id = 1 AND f2.user_id = 2;
```