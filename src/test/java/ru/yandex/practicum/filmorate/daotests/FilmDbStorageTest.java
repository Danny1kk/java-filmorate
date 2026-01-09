package ru.yandex.practicum.filmorate.daotests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.rowmappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class})
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    @Test
    void testCreateAndFindById() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setRatingId(1);

        Film created = filmStorage.addFilm(film);

        assertThat(created.getId()).isNotNull();

        Optional<Film> found = filmStorage.getById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Film");
    }

    @Test
    void testFindAll() {
        Collection<Film> films = filmStorage.getAllFilms();
        assertThat(films).isNotNull();
    }

    @Test
    void testUpdate() {
        Film film = new Film();
        film.setName("Original Name");
        film.setDescription("Original Description");
        film.setReleaseDate(LocalDate.of(2025, 12, 25));
        film.setDuration(120);
        film.setRatingId(1);

        Film created = filmStorage.addFilm(film);

        created.setName("Updated Name");
        filmStorage.updateFilm(created);

        Optional<Film> updated = filmStorage.getById(created.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated Name");
    }

    @Test
    void testDelete() {
        Film film = new Film();
        film.setName("To Delete");
        film.setDescription("Will be deleted");
        film.setReleaseDate(LocalDate.of(2025, 12, 25));
        film.setDuration(120);
        film.setRatingId(1);

        Film created = filmStorage.addFilm(film);
        long id = created.getId();

        filmStorage.delete(id);

        Optional<Film> deleted = filmStorage.getById(id);
        assertThat(deleted).isEmpty();
    }
}