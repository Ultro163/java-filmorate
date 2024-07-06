package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

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
        film.setId(getNextId());
        film.setLikes(new HashSet<>());
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

    private Long getNextId() {
        long currentId = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }
}
