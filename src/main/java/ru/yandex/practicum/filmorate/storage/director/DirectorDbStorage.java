package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
public class DirectorDbStorage extends BaseRepository<Director> {

    private static final String GET_ALL_DIRECTOR_QUERY = "SELECT * FROM directors ORDER BY director_id";
    private static final String GET_DIRECTOR_QUERY = "SELECT * FROM directors WHERE director_id = ?";
    private static final String ADD_DIRECTOR_QUERY = "INSERT INTO directors (name) VALUES (?)";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE directors SET name = ? WHERE director_id = ?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE director_id = ?";
    private static final String GET_DIRECTORS_FOR_FILM_QUERY = """
            SELECT d.*
            FROM films_director as fd
            LEFT JOIN directors as d ON d.director_id = fd.director_id
            WHERE fd.film_id = ?
            """;

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public List<Director> getAllDirectors() {
        return findMany(GET_ALL_DIRECTOR_QUERY);
    }

    public Director getDirector(Long id) {
        return findOne(GET_DIRECTOR_QUERY, id).orElseThrow(() -> new EntityNotFoundException("Director not found"));
    }

    public Director createDirector(Director director) {
        long id = insert(ADD_DIRECTOR_QUERY,
                director.getName());
        director.setId(id);
        return director;
    }

    public Director updateDirector(Director director) {
        update(UPDATE_DIRECTOR_QUERY, director.getName(), director.getId());
        return director;
    }

    public void deleteDirector(Long id) {
        delete(DELETE_DIRECTOR_QUERY, id);
    }

    public Set<Director> getDirectorsForFilm(Long fimId) {
        return new HashSet<>(findMany(GET_DIRECTORS_FOR_FILM_QUERY, fimId));
    }
}
