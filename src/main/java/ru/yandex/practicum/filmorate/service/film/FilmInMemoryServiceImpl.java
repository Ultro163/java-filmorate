package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmInMemoryServiceImpl implements FilmService {

    private final FilmStorage inMemoryFilmStorage;
    private final UserService userInMemoryServiceImpl;
    private static final int DEFAULT_AMOUNT_POPULAR_FILMS = 10;

    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return inMemoryFilmStorage.getFilmById(id);
    }

    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        log.info("Get popular films in memory app");
        Optional<Integer> optionalCount = Optional.ofNullable(count);
        return inMemoryFilmStorage.getAllFilms().stream()
                .filter(film -> film.getLikes() != null)
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(optionalCount.orElse(DEFAULT_AMOUNT_POPULAR_FILMS))
                .toList();
    }

    @Override
    public List<Film> getFilmsWithSortedDirector(Long directorId, String sortBy) {
        return List.of();
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        return List.of();
    }

    public Film createFilm(Film film) {
        return inMemoryFilmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    public void addLikeFilm(long filmId, long userId) {
        log.info("Add like for film {} by the user {}", filmId, userId);
        userInMemoryServiceImpl.getUserById(userId);
        inMemoryFilmStorage.getFilmById(filmId);
        inMemoryFilmStorage.addLikeFilm(filmId, userId);
    }

    public void deleteLikeFromFilm(long filmId, long userId) {
        log.info("Remove like for film {} by the user {}", filmId, userId);
        userInMemoryServiceImpl.getUserById(userId);
        inMemoryFilmStorage.getFilmById(filmId);
        inMemoryFilmStorage.deleteLikeFromFilm(filmId, userId);
    }

    @Override
    public void deleteFilm(long filmId) {
        inMemoryFilmStorage.deleteFilm(filmId);
    }

}