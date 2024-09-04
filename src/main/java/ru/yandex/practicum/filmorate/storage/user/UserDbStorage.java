package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.history.HistoryDbStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private final HistoryDbStorage historyDbStorage;
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users ORDER BY user_id";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_FRIENDS_QUERY = """
            SELECT user2_id as user_id, email, login, name, birthday
            FROM friendship
            INNER JOIN users ON friendship.user2_id = users.user_id
            WHERE friendship.user1_id = ?
            """;
    private static final String FIND_ALL_ID_FRIENDS_QUERY = "SELECT user2_id FROM friendship WHERE user1_id = ?";
    private static final String INSERT_USER_QUERY = """
            INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE_USER_QUERY = """
            UPDATE users SET email = ?, login = ?, name = ?, BIRTHDAY = ? WHERE user_id = ?
            """;
    private static final String INSERT_FRIEND_QUERY = """
            INSERT INTO friendship (user1_id, user2_id, status) VALUES (?, ?, ?)
            """;
    private static final String UPDATE_FRIENDS_STATUS_QUERY = """
            UPDATE friendship SET status = ? WHERE user1_id = ? AND user2_id = ?
            """;
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friendship WHERE user1_id = ? AND user2_id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE user_id = ?";
    private static final String DELETE_USER_REVIEW_QUERY = "DELETE FROM reviews WHERE user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper, HistoryDbStorage historyDbStorage) {
        super(jdbc, mapper);
        this.historyDbStorage = historyDbStorage;
    }

    public List<User> getAllUsers() {
        log.info("Getting all users");
        List<User> users = findMany(FIND_ALL_USERS_QUERY);
        for (User user : users) {
            Set<Long> friendsIds = getALLFriendsIds(user.getId());
            user.setFriends(friendsIds);
        }
        return users;
    }

    public User getUserById(long id) {
        log.info("Getting user with id {}", id);
        Optional<User> userOptional = findOne(FIND_USER_BY_ID_QUERY, id);
        User user = userOptional.orElseThrow(() -> new EntityNotFoundException("User with ID=" + id + " not found"));
        user.setFriends(getALLFriendsIds(id));

        return user;
    }

    public User createUser(User user) {
        log.info("Creating user: {}", user);
        checkingUserName(user);
        long id = insert(
                INSERT_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        log.info("Created user: {}", user);
        return user;
    }

    public User updateUser(User user) {
        log.info("Updating user: {}", user);
        checkingUserName(user);
        update(
                UPDATE_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        user.setFriends(getALLFriendsIds(user.getId()));
        log.info("Updated user: {}", user);
        return user;
    }

    public void addFriends(Long userId, Long friendId, String status) {
        insertData(INSERT_FRIEND_QUERY, userId, friendId, status);
        log.info("Adding friend {} to user {}", friendId, userId);
        saveHistory(friendId, userId, OperationTypes.ADD);
    }

    @Override
    public void deleteFriends(Long userId, Long friendId) {
        deleteTwoKeys(DELETE_FRIEND_QUERY, userId, friendId);
        log.info("Removing friend {} from user {}", friendId, userId);
        saveHistory(friendId, userId, OperationTypes.REMOVE);
    }

    @Override
    public List<User> getFriends(long id) {
        log.info("Getting user's friends {}", id);
        return findMany(FIND_FRIENDS_QUERY, id);
    }

    private Set<Long> getALLFriendsIds(long id) {
        return new HashSet<>(findManyId(FIND_ALL_ID_FRIENDS_QUERY, id));
    }

    @Override
    public void updateFriendsStatus(Long userId, Long friendId, String status) {
        insertData(UPDATE_FRIENDS_STATUS_QUERY, status, userId, friendId);
    }

    private void checkingUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Name is not present, Username set to {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private void saveHistory(Long id, Long userId, OperationTypes operationTypes) {
        historyDbStorage.addEvent(Event.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .eventType(EventTypes.FRIEND)
                .operation(operationTypes)
                .entityId(id)
                .build());
    }

    public void deleteUser(long userId) {
        delete(DELETE_USER_REVIEW_QUERY, userId);
        delete(DELETE_USER_QUERY, userId);
    }
}