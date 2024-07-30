package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private static final String FIND_GENRES_FOR_FILMS = """
            SELECT g.* FROM genre_film AS gf
            LEFT JOIN genres g ON gf.genre_id = g.genre_id
            WHERE gf.film_id = ?
            """;
    private static final String FIND_ALL_GENRES = "SELECT * FROM genres ORDER BY genre_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ? ORDER BY genre_id";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Set<Genre> getGenresForFilm(long filmId) {
        return findMany(FIND_GENRES_FOR_FILMS, filmId).stream().sorted(Comparator.comparingLong(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Genre getGenreById(int genreId) {
        Optional<Genre> genreOptional = findOne(FIND_BY_ID_QUERY, genreId);
        return genreOptional.orElseThrow(() -> new EntityNotFoundException("Genre with ID=" + genreId + " not found"));
    }

    @Override
    public List<Genre> getAllGenre() {
        return findMany(FIND_ALL_GENRES);
    }
}