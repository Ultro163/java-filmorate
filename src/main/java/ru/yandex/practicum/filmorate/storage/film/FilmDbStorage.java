package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.history.HistoryDbStorage;
import ru.yandex.practicum.filmorate.util.validator.FilmValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_FILMS_QUERY = "SELECT * FROM films ORDER BY film_id";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM films WHERE film_id = ?";
    private static final String GET_LIKES_FILM_QUERY = """
            SELECT user_id
            FROM film_likes_users
            WHERE film_id = ?
            """;
    private static final String ADD_LIKE_FILM_QUERY = """
            INSERT INTO film_likes_users (film_id, user_id)
            VALUES (?, ?)
            """;
    private static final String ADD_FILM_QUERY = """
            INSERT INTO films (name, description, release_date, duration, rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String INSERT_GENRE_FILM_QUERY = "INSERT INTO genre_film (film_id,genre_id) VALUES (?,?)";
    private static final String UPDATE_FILM_QUERY = """
            UPDATE films SET name = ?, description = ?, release_date = ?,
            duration = ?, rating_id = ?
            WHERE film_id = ?""";
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM film_likes_users WHERE film_id = ? AND user_id = ?
            """;
    private static final String DELETE_GENRE_FILM_QUERY = "DELETE FROM genre_film WHERE film_id = ?";
    private static final String DELETE_FILM_QUERY = "DELETE FROM FILMS WHERE FILM_ID = ?";
    private static final String DELETE_FILM_REVIEW_QUERY = "DELETE FROM REVIEWS WHERE FILM_ID = ?";

    private final HistoryDbStorage historyDbStorage;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final FilmValidator filmValidator;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, HistoryDbStorage historyDbStorage,
                         MpaService mpaService,
                         GenreService genreService, FilmValidator filmValidator) {
        super(jdbc, mapper);
        this.historyDbStorage = historyDbStorage;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.filmValidator = filmValidator;
    }

    public List<Film> getAllFilms() {
        log.info("Getting all films");
        List<Film> films = findMany(FIND_ALL_FILMS_QUERY);
        films.forEach(film -> film.setMpa(mpaService.getMpaById(film.getMpa().getId())));
        films.forEach(film -> film.setGenres(genreService.getGenresForFilm(film.getId())));
        films.forEach(film -> film.setLikes(getUsersIdWhoLikeFilm(film.getId())));
        return films;
    }

    public Set<Long> getUsersIdWhoLikeFilm(long filmId) {
        return new HashSet<>(findManyId(GET_LIKES_FILM_QUERY, filmId));
    }

    public Film getFilmById(long id) {
        log.info("Getting film with id {}", id);
        Optional<Film> userOptional = findOne(FIND_FILM_BY_ID_QUERY, id);
        Film film = userOptional.orElseThrow(() -> new EntityNotFoundException("Film with ID=" + id + " not found"));
        film.setGenres(genreService.getGenresForFilm(film.getId()));
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        film.setLikes(getUsersIdWhoLikeFilm(id));
        return film;
    }

    public Film createFilm(Film film) {
        log.info("Creating film: {}", film);
        filmValidator.verifyFilmIsValid(film);
        long id = insert(
                ADD_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.getGenres().forEach(genre -> insertData(INSERT_GENRE_FILM_QUERY, id, genre.getId()));
        film.setId(id);
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        film.setGenres(genreService.getGenresForFilm(id));
        log.info("Film created: {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        log.info("Updating film: {}", film);
        filmValidator.verifyFilmIsValid(film);
        update(
                UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        if (film.getGenres() != null) {
            delete(DELETE_GENRE_FILM_QUERY, film.getId());
            film.getGenres().forEach(genre -> insertData(INSERT_GENRE_FILM_QUERY, film.getId(), genre.getId()));
        }
        film.setLikes(getUsersIdWhoLikeFilm(film.getId()));
        log.info("Updated film: {}", film);
        return film;
    }

    @Override
    public void addLikeFilm(long filmId, long userId) {
        insertData(ADD_LIKE_FILM_QUERY, filmId, userId);
        log.info("Added like for film {} by the user {}", filmId, userId);
        saveHistory(filmId, userId, OperationTypes.ADD);
    }

    @Override
    public void deleteLikeFromFilm(long filmId, long userId) {
        deleteTwoKeys(DELETE_LIKE_QUERY, filmId, userId);
        log.info("Removed like for film {} by the user {}", filmId, userId);
        saveHistory(filmId, userId, OperationTypes.REMOVE);
    }

    private void saveHistory(Long id, Long userId, OperationTypes operationTypes) {
        historyDbStorage.addEvent(Event.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .eventType(EventTypes.LIKE)
                .operation(operationTypes)
                .entityId(id)
                .build());
    }

    public void deleteFilm(long filmId) {
        delete(DELETE_FILM_REVIEW_QUERY, filmId);
        delete(DELETE_FILM_QUERY, filmId);
    }
}