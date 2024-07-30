package ru.yandex.practicum.filmorate.util.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmValidator {

    MpaService mpaService;
    GenreService genreService;

    public FilmValidator(GenreService genreService, MpaService mpaService) {
        this.genreService = genreService;
        this.mpaService = mpaService;
    }

    public void verifyFilmIsValid(Film film) {
        mpaService.getAllMpa().stream().filter(mpa -> film.getMpa().getId() == mpa.getId()).findFirst()
                .orElseThrow(() -> new ValidationException("This MPA rating does not exist"));

        Set<Integer> validGenreIds = genreService.getAllGenre().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        film.getGenres().stream()
                .map(Genre::getId)
                .filter(id -> !validGenreIds.contains(id))
                .findAny()
                .ifPresent(id -> {
                    throw new ValidationException("Genre with ID " + id + " does not exist");
                });
    }
}