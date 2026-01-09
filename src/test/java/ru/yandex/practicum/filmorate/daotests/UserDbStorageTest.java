package ru.yandex.practicum.filmorate.daotests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.rowmappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void testCreateAndFindById() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.addUser(user);

        assertThat(created.getId()).isNotNull();

        Optional<User> found = userStorage.getById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void testGetAllUsers() {
        Collection<User> users = userStorage.getAllUsers();
        assertThat(users).isNotNull();
    }

    @Test
    void testUpdate() {
        User user = new User();
        user.setEmail("user_test@email.com");
        user.setLogin("login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1992, 2, 10));

        User created = userStorage.addUser(user);

        created.setName("Updated Name");
        userStorage.updateUser(created);

        Optional<User> updated = userStorage.getById(created.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated Name");
    }

    @Test
    void testDelete() {
        User user = new User();
        user.setEmail("delete@email.com");
        user.setLogin("delete");
        user.setName("To Delete");
        user.setBirthday(LocalDate.of(1992, 2, 10));

        User created = userStorage.addUser(user);
        long id = created.getId();

        userStorage.delete(id);

        Optional<User> deleted = userStorage.getById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    void testAddAndRemoveFriend() {
        User user1 = new User();
        user1.setEmail("user_test_1@email.com");
        user1.setLogin("user_test_1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1993, 10, 15));

        User user2 = new User();
        user2.setEmail("user_teat_2@email.com");
        user2.setLogin("user_test_2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1994, 5, 19));

        User created1 = userStorage.addUser(user1);
        User created2 = userStorage.addUser(user2);

        userStorage.addFriends(created1.getId(), created2.getId());

        userStorage.loadFriends(created1);
        assertThat(created1.getFriends()).contains(created2.getId());

        userStorage.removeFriends(created1.getId(), created2.getId());

        created1.getFriends().clear();
        userStorage.loadFriends(created1);
        assertThat(created1.getFriends()).doesNotContain(created2.getId());
    }
}