package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.history.FeedService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userDbServiceImpl;
    private final FeedService feedService;
    private final FilmService filmDbServiceImpl;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        return userDbServiceImpl.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable long id) {
        return userDbServiceImpl.getUserById(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendationsFilmsForUser(@PathVariable Long id) {
        return filmDbServiceImpl.getRecommendationsFilmsForUser(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        return userDbServiceImpl.createUser(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@Valid @RequestBody User user) {
        return userDbServiceImpl.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userDbServiceImpl.addFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        userDbServiceImpl.deleteFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriends(@PathVariable long id) {
        return userDbServiceImpl.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsersWithFriends(@PathVariable long id, @PathVariable long otherId) {
        return userDbServiceImpl.getListMutualFriends(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable("id") long id) {
        return feedService.getFeed(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") long id) {
        userDbServiceImpl.deleteUser(id);
    }

}