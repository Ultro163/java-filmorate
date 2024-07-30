package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    Set<Genre> getGenresForFilm(long filmId);

    Genre getGenreById(int genreId);

    List<Genre> getAllGenre();
}