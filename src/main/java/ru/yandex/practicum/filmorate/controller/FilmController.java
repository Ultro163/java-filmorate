package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Getting all films");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Creat film: {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            log.warn("Film id is null");
            throw new ValidationException("Film id is null");
        }
        if (films.get(film.getId()) == null) {
            log.warn("Film not found");
            throw new ValidationException("Film not found");
        }
        log.info("Updated film: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    public void clear() {
        films.clear();
    }

    private Long getNextId() {
        long currentId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentId;
    }
}
