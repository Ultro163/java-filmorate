package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseRepository<Mpa> implements MpaStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM rating_mpa ORDER BY rating_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM rating_mpa WHERE rating_id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Mpa getMpaById(int id) {
        Optional<Mpa> mpaOptional = findOne(FIND_BY_ID_QUERY, id);
        return mpaOptional.orElseThrow(() -> new EntityNotFoundException("Rating mpa with id=" + id + " not found"));
    }

    @Override
    public List<Mpa> getAllMpa() {
        return findMany(FIND_ALL_QUERY);
    }
}