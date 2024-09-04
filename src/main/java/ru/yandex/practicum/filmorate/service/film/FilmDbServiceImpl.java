package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.director.DirectorService;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmDbServiceImpl implements FilmService {

    private final FilmStorage filmDbStorage;
    private final UserService userDbServiceImpl;
    private final DirectorService directorService;
    private final GenreService genreService;
    private static final int DEFAULT_AMOUNT_POPULAR_FILMS = 10;

    public List<Film> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmDbStorage.getFilmById(id);
    }

    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        log.info("Get popular films");
        Optional<Integer> optionalCount = Optional.ofNullable(count);
        return getAllFilms()
                .stream()
                //.filter(film -> !film.getLikes().isEmpty())
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .filter(film -> genreId == null || film.getGenres().contains(genreService.getGenreById(genreId)))
                .filter(film -> year == null || film.getReleaseDate().getYear() == year)
                .limit(optionalCount.orElse(DEFAULT_AMOUNT_POPULAR_FILMS))
                .collect(Collectors.toList());
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

    @Override
    public List<Film> searchFilms(String query, String by) {
        String[] sort = by.split(",");
        boolean director = Arrays.asList(sort).contains("director");
        boolean title = Arrays.asList(sort).contains("title");

        return getAllFilms().stream()
                .filter(film -> {
                    boolean matchesDirector = film.getDirectors().stream()
                            .anyMatch(director1 -> director1.getName().toLowerCase().contains(query.toLowerCase()));

                    boolean matchesTitle = film.getName().toLowerCase().contains(query.toLowerCase());

                    return (title && matchesTitle) || (director && matchesDirector);
                })
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .toList();
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmDbStorage.getCommonFilms(userId, friendId);
    }

    @Override
    public List<Film> getRecommendationsFilmsForUser(Long id) {
        return filmDbStorage.getRecommendationsFilmsForUser(id);
    }

    public Film createFilm(Film film) {
        return filmDbStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId());
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