package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Integer, Genre> genreMap;
    private final Map<Integer, Mpa> mpaMap;

    InMemoryFilmStorage() {
        Map<Integer, Genre> genre = new HashMap<>();
        genre.put(1, new Genre(1, "Комедия"));
        genre.put(2, new Genre(2, "Драма"));
        genre.put(3, new Genre(3, "Мультфильм"));
        genre.put(4, new Genre(4, "Триллер"));
        genre.put(5, new Genre(5, "Документальный"));
        genre.put(6, new Genre(6, "Боевик"));
        Map<Integer, Mpa> mpa = new HashMap<>();
        mpa.put(1, new Mpa(1, "G"));
        mpa.put(2, new Mpa(2, "PG"));
        mpa.put(3, new Mpa(3, "PG-13"));
        mpa.put(4, new Mpa(4, "R"));
        mpa.put(5, new Mpa(5, "NC-17"));
        this.genreMap = genre;
        this.mpaMap = mpa;
    }

    public List<Film> getAllFilms() {
        log.info("Getting all films");
        return new ArrayList<>(films.values());
    }

    public Film getFilmById(long id) {
        log.info("Getting film with id {}", id);
        if (films.get(id) == null) throw new EntityNotFoundException("Film with id " + id + " not found");
        return films.get(id);
    }

    public Film createFilm(Film film) {
        log.info("Creating film: {}", film);
        mpaMap.values().stream().filter(mpa -> film.getMpa().getId() == mpa.getId()).findFirst()
                .orElseThrow(() -> new ValidationException("This MPA rating does not exist"));
        film.setId(getNextId());
        film.setMpa(mpaMap.get(film.getMpa().getId()));
        if (!film.getGenres().isEmpty()) {
            Set<Integer> validGenreIds = genreMap.values().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            for (Genre genre : film.getGenres()) {
                if (!validGenreIds.contains(genre.getId())) {
                    throw new ValidationException("Genre with ID " + genre.getId() + " does not exist");
                }
            }
            film.setGenres(film.getGenres().stream().map(Genre::getId)
                    .map(genreMap::get).sorted(Comparator.comparingLong(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }
        films.put(film.getId(), film);
        log.info("Film created: {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        log.info("Updating film: {}", film);
        if (film.getId() == null) {
            throw new ValidationException("To update the movie you need to transfer ID");
        }
        if (films.get(film.getId()) == null) {
            throw new EntityNotFoundException("Film with id " + film.getId() + " not found");
        }
        log.info("Updated film: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLikeFilm(long filmId, long userId) {
        Film film = getFilmById(filmId);
        film.getLikes().add(userId);
        log.info("Added like for film {} by the user {}", filmId, userId);
    }

    @Override
    public void deleteLikeFromFilm(long filmId, long userId) {
        Film film = getFilmById(filmId);
        film.getLikes().remove(userId);
        log.info("Removed like for film {} by the user {}", filmId, userId);
    }

    @Override
    public void deleteFilm(long filmId) {
        getFilmById(filmId);
        films.remove(filmId);
    }

    private Long getNextId() {
        long currentId = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }
}