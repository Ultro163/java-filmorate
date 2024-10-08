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
import ru.yandex.practicum.filmorate.service.director.DirectorService;
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
    private static final String FIND_COMMON_FILMS_QUERY = """
            with user_films as (SELECT film_id, count(*) as cnt
                                FROM film_likes_users
                                WHERE user_id = ?
                                GROUP BY film_id
                                ),
                 friend_films as (SELECT film_id, count(*) as cnt
                                 FROM film_likes_users
                                 WHERE user_id = ?
                                 GROUP BY film_id
                                 )
            SELECT f.*
            FROM films as f
            JOIN user_films as uf ON f.film_id = uf.film_id
            JOIN friend_films as ff ON f.film_id = ff.film_id
            ORDER BY uf.cnt DESC
            """;
    private static final String FIND_RECOMMENDATIONS_FOR_USER_QUERY = """
            WITH prep AS (
                SELECT l1.USER_ID, COUNT(*) AS cnt
                FROM film_likes_users l1
                INNER JOIN film_likes_users l2 ON l2.FILM_ID = l1.FILM_ID
                AND l2.USER_ID = ?
                WHERE l1.USER_ID != ?
                GROUP BY l1.USER_ID
                ORDER BY COUNT(*) DESC
                )
            SELECT f.*
            FROM film_likes_users l1
            INNER JOIN prep p ON p.USER_ID = l1.USER_ID
            LEFT JOIN film_likes_users l2 ON l2.FILM_ID = l1.FILM_ID
                               AND l2.USER_ID = ?
            INNER JOIN films f ON f.FILM_ID = l1.FILM_ID
            WHERE l2.FILM_ID IS NULL
            ORDER BY p.cnt DESC
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
    private static final String INSERT_DIRECTORS_FILM_QUERY = """
            INSERT INTO films_director (film_id, director_id)
            VALUES (?, ?)
            """;
    private static final String UPDATE_FILM_QUERY = """
            UPDATE films SET name = ?, description = ?, release_date = ?,
            duration = ?, rating_id = ?
            WHERE film_id = ?""";
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM film_likes_users WHERE film_id = ? AND user_id = ?
            """;
    private static final String DELETE_GENRE_FILM_QUERY = "DELETE FROM genre_film WHERE film_id = ?";
    private static final String DELETE_DIRECTORS_FILM_QUERY = "DELETE FROM films_director WHERE film_id = ?";
    private static final String DELETE_FILM_QUERY = "DELETE FROM FILMS WHERE FILM_ID = ?";
    private static final String DELETE_FILM_REVIEW_QUERY = "DELETE FROM REVIEWS WHERE FILM_ID = ?";

    private final HistoryDbStorage historyDbStorage;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final DirectorService directorService;
    private final FilmValidator filmValidator;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, HistoryDbStorage historyDbStorage,
                         MpaService mpaService,
                         GenreService genreService, DirectorService directorService, FilmValidator filmValidator) {
        super(jdbc, mapper);
        this.historyDbStorage = historyDbStorage;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.directorService = directorService;
        this.filmValidator = filmValidator;
    }

    public List<Film> getAllFilms() {
        log.info("Getting all films");
        return fillFilmMetadata(findMany(FIND_ALL_FILMS_QUERY));
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
        film.setDirectors(directorService.getDirectorsForFilm(film.getId()));
        return film;
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return fillFilmMetadata(findMany(FIND_COMMON_FILMS_QUERY, userId, friendId));
    }

    @Override
    public List<Film> getRecommendationsFilmsForUser(Long id) {
        return fillFilmMetadata(findMany(FIND_RECOMMENDATIONS_FOR_USER_QUERY, id, id, id));
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
        film.getDirectors().forEach(director -> insertData(INSERT_DIRECTORS_FILM_QUERY, id, director.getId()));
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
        if (film.getDirectors() != null) {
            delete(DELETE_DIRECTORS_FILM_QUERY, film.getId());
            film.getDirectors().forEach(director -> insertData(INSERT_DIRECTORS_FILM_QUERY, film.getId(), director.getId()));
        }
        film.setDirectors(directorService.getDirectorsForFilm(film.getId()));
        film.setLikes(getUsersIdWhoLikeFilm(film.getId()));
        film.setGenres(genreService.getGenresForFilm(film.getId()));
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
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
        log.info("Removing film with ID = {}", filmId);
        delete(DELETE_FILM_REVIEW_QUERY, filmId);
        delete(DELETE_FILM_QUERY, filmId);
        log.info("Removed film with ID = {}", filmId);
    }

    private List<Film> fillFilmMetadata(List<Film> films) {
        films.forEach(film -> film.setMpa(mpaService.getMpaById(film.getMpa().getId())));
        films.forEach(film -> film.setGenres(genreService.getGenresForFilm(film.getId())));
        films.forEach(film -> film.setLikes(getUsersIdWhoLikeFilm(film.getId())));
        films.forEach(film -> film.setDirectors(directorService.getDirectorsForFilm(film.getId())));
        return films;
    }
}