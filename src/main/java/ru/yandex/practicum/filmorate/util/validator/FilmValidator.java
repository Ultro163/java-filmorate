package ru.yandex.practicum.filmorate.util.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmValidator {

    private final MpaService mpaService;
    private final GenreService genreService;

    public void verifyFilmIsValid(Film film) {
        mpaService.getAllMpa().stream().filter(mpa -> film.getMpa().getId() == mpa.getId()).findFirst()
                .orElseThrow(() -> new ValidationException("This MPA rating does not exist"));

        List<Integer> validGenreIds = genreService.getAllGenre().stream().map(Genre::getId).toList();
        film.getGenres().stream()
                .map(Genre::getId)
                .filter(id -> !validGenreIds.contains(id))
                .findAny()
                .ifPresent(id -> {
                    throw new ValidationException("Genre with ID " + id + " does not exist");
                });
    }
}