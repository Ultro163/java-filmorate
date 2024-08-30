package ru.yandex.practicum.filmorate.storage.history;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Collection;

@Slf4j
@Repository
public class HistoryDbStorage extends BaseRepository<Event> {
    // SQL-запросы
    private static final String INSERT_QUERY = "INSERT INTO HISTORY_ACTIONS(USER_ID, TIME_ACTION, TYPE, OPERATION," +
            " ENTITY_ID)" +
            " VALUES (?,?,?,?,?)";
    private static final String FIND_EVENT_BY_USER_ID_QUERY = "SELECT * FROM HISTORY_ACTIONS WHERE USER_ID = ?";

    public HistoryDbStorage(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public Event addEvent(Event event) {
        long id = insert(
                INSERT_QUERY,
                event.getUserId(),
                event.getTimestamp(),
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getEntityId()
        );
        event.setEventId(id);
        log.info("Создан ЭВЕНТ c ID {}. Добавлено действие {} {} = {} пользователя c ID = {}", event.getEventId(), event.getEventType().toString(),
                event.getOperation().toString(), event.getEntityId(), event.getUserId());
        return event;
    }

    public Collection<Event> getEventsByUser(long userId) {
        return findMany(
                FIND_EVENT_BY_USER_ID_QUERY,
                userId
        );
    }
}