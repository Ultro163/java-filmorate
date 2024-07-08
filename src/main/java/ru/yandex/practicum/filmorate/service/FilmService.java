package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final static int DEFAULT_AMOUNT_POPULAR_FILMS = 10;

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getPopularFilms(Optional<Integer> count) {
        log.info("Get popular films");
        return filmStorage.getAllFilms().stream()
                .filter(film -> film.getLikes() != null)
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(count.orElse(DEFAULT_AMOUNT_POPULAR_FILMS))
                .toList();

    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLikeFilm(long filmId, long userId) {
        log.info("Add like film {} to user {}", filmId, userId);
        userService.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
    }

    public void deleteLikeFromFilm(long filmId, long userId) {
        log.info("Remove like film {} from user {}", filmId, userId);
        userService.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
    }
}
