package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();

    public List<User> getAllUsers() {
        log.info("Getting all users");
        return new ArrayList<>(users.values());
    }

    public User getUserByID(long id) {
        log.info("Getting user with id {}", id);
        if (users.get(id) == null) throw new EntityNotFoundException("User with id " + id + " not found");
        return users.get(id);
    }

    public User createUser(User user) {
        log.info("Creating user: {}", user);
        checkingUserName(user);
        user.setId(getNextId());
        user.setFriends(new HashSet<>());
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
        log.info("Updated user: {}", user);
        checkingUserName(user);
        users.put(user.getId(), user);
        return user;
    }

    public void checkingUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Name is not present, Username set to {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private Long getNextId() {
        long currentId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }
}
