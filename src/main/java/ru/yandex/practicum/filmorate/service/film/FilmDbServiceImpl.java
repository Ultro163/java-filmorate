package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.director.DirectorService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmDbServiceImpl implements FilmService {

    private final FilmStorage filmDbStorage;
    private final UserService userDbServiceImpl;
    private final DirectorService directorService;
    private static final int DEFAULT_AMOUNT_POPULAR_FILMS = 10;

    public List<Film> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmDbStorage.getFilmById(id);
    }

    public List<Film> getPopularFilms(Integer count) {
        log.info("Get popular films");
        Optional<Integer> optionalCount = Optional.ofNullable(count);
        return filmDbStorage.getAllFilms().stream()
                .filter(film -> film.getLikes() != null)
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(optionalCount.orElse(DEFAULT_AMOUNT_POPULAR_FILMS))
                .toList();
    }

    @Override
    public List<Film> getFilmsWithSortedDirector(Long directorId, String sortBy) {
        return getAllFilms().stream()
                .filter(film -> film.getDirectors().contains(directorService.getDirector(directorId)))
                .sorted((film1, film2) -> switch (sortBy) {
                    case "year" -> Integer.compare(film1.getReleaseDate().getYear(), film2.getReleaseDate().getYear());
                    case "likes" -> Integer.compare(film2.getLikes().size(), film1.getLikes().size());
                    case null -> 0;
                    default -> throw new ValidationException("Invalid value: " + sortBy);
                })
                .toList();
    }

    public Film createFilm(Film film) {
        return filmDbStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmDbStorage.updateFilm(film);
    }

    public void addLikeFilm(long filmId, long userId) {
        log.info("Add like for film {} by the user {}", filmId, userId);
        userDbServiceImpl.getUserById(userId);
        filmDbStorage.getFilmById(filmId);
        filmDbStorage.addLikeFilm(filmId, userId);
    }

    public void deleteLikeFromFilm(long filmId, long userId) {
        log.info("Remove like for film {} by the user {}", filmId, userId);
        userDbServiceImpl.getUserById(userId);
        filmDbStorage.getFilmById(filmId);
        filmDbStorage.deleteLikeFromFilm(filmId, userId);
    }

    @Override
    public void deleteFilm(long filmId) {
        filmDbStorage.deleteFilm(filmId);
    }
}