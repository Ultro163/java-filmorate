package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Qualifier("FilmInMemoryServiceImpl")
public class FilmInMemoryServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private static final int DEFAULT_AMOUNT_POPULAR_FILMS = 10;

    public FilmInMemoryServiceImpl(@Qualifier("InMemoryFilmStorage") FilmStorage filmStorage,
                                   @Qualifier("UserInMemoryServiceImpl") UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getPopularFilms(Integer count) {
        log.info("Get popular films in memory app");
        Optional<Integer> optionalCount = Optional.ofNullable(count);
        return filmStorage.getAllFilms().stream()
                .filter(film -> film.getLikes() != null)
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(optionalCount.orElse(DEFAULT_AMOUNT_POPULAR_FILMS))
                .toList();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLikeFilm(long filmId, long userId) {
        log.info("Add like for film {} by the user {}", filmId, userId);
        userService.getUserById(userId);
        filmStorage.getFilmById(filmId);
        filmStorage.addLikeFilm(filmId, userId);
    }

    public void deleteLikeFromFilm(long filmId, long userId) {
        log.info("Remove like for film {} by the user {}", filmId, userId);
        userService.getUserById(userId);
        filmStorage.getFilmById(filmId);
        filmStorage.deleteLikeFromFilm(filmId, userId);
    }
}