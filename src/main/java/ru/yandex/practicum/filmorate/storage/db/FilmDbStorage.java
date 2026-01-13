package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.rowmappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Component("filmDbStorage")
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper mapper;
    private final ResourceLoader resourceLoader;

    @Override
    public Film addFilm(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String insertTableQuery = "INSERT INTO films(name, description, release_date, duration, rating_id)" +
                " VALUES (?, ?, ?, ?, ?) ";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    insertTableQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getRatingId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        saveGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        String updateTableQuery = "UPDATE films SET name = ?, description = ?, release_date = ?," +
                " duration = ?, rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(updateTableQuery,
                newFilm.getName(),
                newFilm.getDescription(),
                Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                newFilm.getRatingId(),
                newFilm.getId()
        );
        deleteGenres(newFilm.getId());
        saveGenres(newFilm);
        return newFilm;
    }

    @Override
    public void delete(long id) {
        deleteGenres(id);
        deleteAllLikes(id);
        String deleteTableQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(deleteTableQuery, id);
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM films", mapper);
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = "SELECT f. * FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, mapper, count);
    }

    @Override
    public Optional<Film> getById(long id) {
        try {
            Film result = jdbcTemplate.queryForObject(
                    "SELECT * FROM films WHERE film_id = ?", mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void addLike(long filmId, long userId) {
        String insertGenreQuery = "INSERT INTO likes(film_id, user_id) VALUES(?, ?)";
        jdbcTemplate.update(insertGenreQuery, filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        String query = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(query, filmId, userId);
    }

    private void deleteAllLikes(Long filmId) {
        String query = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(query, filmId);
    }

    private void saveGenres(Film film) {
        if (film.getGenreIds() == null || film.getGenreIds().isEmpty())
            return;
        String insertGenreQuery = "INSERT INTO film_genres(film_id, genre_id) VALUES(?, ?)";
        film.getGenreIds().forEach(genreId ->
                jdbcTemplate.update(insertGenreQuery, film.getId(), genreId));
    }

    private void deleteGenres(long filmId) {
        String deleteQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteQuery, filmId);
    }

    @Override
    public Map<Long, Set<Integer>> getGenresForFilms(List<Long> filmIds) {
        if (filmIds.isEmpty()) return Collections.emptyMap();

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = "SELECT film_id, genre_id FROM film_genres WHERE film_id IN (" + inSql + ")";

        return jdbcTemplate.query(sql, (rs) -> {
            Map<Long, Set<Integer>> result = new HashMap<>();
            while (rs.next()) {
                long filmId = rs.getLong("film_id");
                int genreId = rs.getInt("genre_id");
                result.computeIfAbsent(filmId, k -> new HashSet<>()).add(genreId);
            }
            return result;
        }, filmIds.toArray());
    }

    @Override
    public Map<Long, Set<Long>> getLikesForFilms(List<Long> filmIds) {
        if (filmIds.isEmpty()) return Collections.emptyMap();

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = "SELECT film_id, user_id FROM likes WHERE film_id IN (" + inSql + ")";

        return jdbcTemplate.query(sql, (rs) -> {
            Map<Long, Set<Long>> result = new HashMap<>();
            while (rs.next()) {
                long filmId = rs.getLong("film_id");
                long userId = rs.getLong("user_id");
                result.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
            }
            return result;
        }, filmIds.toArray());
    }
}