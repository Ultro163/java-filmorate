package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        log.info("Getting all films");
        return new ArrayList<>(films.values());
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
            throw new ConditionsNotMetException("Film id is null");
        }
        if (films.get(film.getId()) == null) {
            log.warn("Film not found");
            throw new EntityNotFoundException("Film not found");
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
