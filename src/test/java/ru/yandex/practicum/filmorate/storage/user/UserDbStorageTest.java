package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.history.FeedService;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;
import ru.yandex.practicum.filmorate.service.user.UserDbServiceImpl;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.history.HistoryDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.*;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.util.validator.FilmValidator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class, UserDbServiceImpl.class,
        FilmDbStorage.class, FilmRowMapper.class, GenreDbStorage.class, GenreRowMapper.class, GenreService.class,
        MpaDbStorage.class, MpaRowMapper.class, MpaService.class, FilmValidator.class, HistoryDbStorage.class,
        FeedService.class, EventRowMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    private void addUserInDb() {
        final User user = User.builder()
                .id(1L)
                .email("Skuf@maila.net")
                .login("MegaPusher")
                .name("")
                .birthday(LocalDate.of(1987, 7, 12))
                .friends(new HashSet<>())
                .build();
        userDbStorage.createUser(user);
    }

    private void addTwoUserInDb() {
        final User user = User.builder()
                .id(1L)
                .email("Skuf@maila.net")
                .login("MegaPusher")
                .name("")
                .birthday(LocalDate.of(1987, 7, 12))
                .friends(new HashSet<>())
                .build();
        final User user2 = User.builder()
                .email("karamba@maila.net")
                .login("MegaRak")
                .name("")
                .birthday(LocalDate.of(1987, 7, 12))
                .friends(new HashSet<>())
                .build();
        userDbStorage.createUser(user);
        userDbStorage.createUser(user2);
    }

    @Test
    void getUserById() {
        addUserInDb();
        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void createUser() {
        final User user = User.builder()
                .email("Skuf@maila.net")
                .login("MegaPusher")
                .name("")
                .birthday(LocalDate.of(1987, 7, 12))
                .friends(new HashSet<>())
                .build();
        User responseUser = userDbStorage.createUser(user);
        assertEquals(1, responseUser.getId());
        assertEquals("MegaPusher", responseUser.getName());
    }

    @Test
    void updateUser() {
        addUserInDb();
        User user = User.builder()
                .id(1L)
                .email("Skuf@maila.net")
                .login("MegaPusher")
                .name("paginator777")
                .birthday(LocalDate.of(1987, 7, 12))
                .friends(new HashSet<>())
                .build();
        User updateUser = userDbStorage.updateUser(user);
        assertEquals("paginator777", updateUser.getName());
    }

    @Test
    void addFriends() {
        addTwoUserInDb();
        userDbStorage.addFriends(1L, 2L, "unconfirmed");
        assertEquals(Set.of(2L), userDbStorage.getUserById(1L).getFriends());
    }

    @Test
    void deleteFriends() {
        addTwoUserInDb();
        userDbStorage.addFriends(1L, 2L, "unconfirmed");
        userDbStorage.deleteFriends(1L, 2L);
        Set<Long> list = userDbStorage.getUserById(1).getFriends();
        assertTrue(list.isEmpty());
    }

    @Test
    void getFriends() {
        addTwoUserInDb();
        userDbStorage.addFriends(1L, 2L, "unconfirmed");
        assertEquals(List.of(userDbStorage.getUserById(2L)), userDbStorage.getFriends(1L));
    }
}