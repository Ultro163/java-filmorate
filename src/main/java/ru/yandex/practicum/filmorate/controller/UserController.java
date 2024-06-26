package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        log.info("Getting all users");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Creating user: {}", user);
        checkingUserName(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.warn("User id is null");
            throw new ValidationException("User id is null");
        }
        if (users.get(user.getId()) == null) {
            log.warn("User id not found");
            throw new EntityNotFoundException("User not found");
        }
        log.info("Updating user: {}", user);
        checkingUserName(user);
        users.put(user.getId(), user);
        return user;
    }

    private void checkingUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Change Username to {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    public void clearUsers() {
        users.clear();
    }

    private Long getNextId() {
        long currentId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentId;
    }
}
