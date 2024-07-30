package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Set<Genre> getGenresForFilm(long filmId) {
        return genreStorage.getGenresForFilm(filmId);
    }

    public Genre getGenreById(int genreId) {
        return genreStorage.getGenreById(genreId);
    }

    public List<Genre> getAllGenre() {
        return genreStorage.getAllGenre();
    }
}