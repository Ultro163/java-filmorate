package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();

    public List<User> getAllUsers() {
        log.info("Getting all users");
        return new ArrayList<>(users.values());
    }

    public User getUserById(long id) {
        log.info("Getting user with id {}", id);
        if (users.get(id) == null) throw new EntityNotFoundException("User with id " + id + " not found");
        return users.get(id);
    }

    public User createUser(User user) {
        log.info("Creating user: {}", user);
        checkingUserName(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Created user: {}", user);
        return user;
    }

    public User updateUser(User user) {
        log.info("Updating user: {}", user);
        if (user.getId() == null) {
            throw new ValidationException("To update the user you need to transfer ID");
        }
        if (users.get(user.getId()) == null) {
            throw new EntityNotFoundException("User with id " + user.getId() + " not found");
        }
        checkingUserName(user);
        users.put(user.getId(), user);
        log.info("Updated user: {}", user);
        return user;
    }

    private void checkingUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Name is not present, Username set to {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    @Override
    public void addFriends(Long userId, Long friendId, String status) {
        User user = getUserById(userId);
        getUserById(friendId);

        user.getFriends().add(friendId);
        log.info("Adding friend {} to user {}", friendId, userId);
    }

    @Override
    public void deleteFriends(Long userId, Long friendId) {
        User user = getUserById(userId);
        getUserById(friendId);

        user.getFriends().remove(friendId);
        log.info("Removing friend {} from user {}", friendId, userId);
    }

    @Override
    public List<User> getFriends(long id) {
        log.info("Getting user's friends {}", id);
        getUserById(id);
        return new ArrayList<>(users.get(id).getFriends().stream().map(users::get).toList());
    }

    @Override
    public void updateFriendsStatus(Long userId, Long friendId, String status) {
        log.warn("this method is not supported by this class");
    }

    private Long getNextId() {
        long currentId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }
}
