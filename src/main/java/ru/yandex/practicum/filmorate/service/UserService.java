package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) {
        return userStorage.getUserByID(id);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriends(Long userId, Long friendId) {
        log.info("Adding friend {} to user {}", friendId, userId);
        User user = userStorage.getUserByID(userId);
        User friend = userStorage.getUserByID(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void deleteFriends(Long userId, Long friendId) {
        log.info("Removing friend {} from user {}", friendId, userId);
        User user = userStorage.getUserByID(userId);
        User friend = userStorage.getUserByID(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(long id) {
        log.info("Getting friends {}", id);
        return userStorage.getUserByID(id).getFriends().stream().map(userStorage::getUserByID).toList();
    }

    public List<User> getListMutualFriends(Long userId, Long otherUserId) {
        log.info("Getting list for mutual friends {}", otherUserId);
        List<Long> otherUserList = new ArrayList<>(userStorage.getUserByID(otherUserId).getFriends());

        return userStorage.getUserByID(userId).getFriends().stream()
                .filter(otherUserList::contains)
                .map(userStorage::getUserByID).toList();
    }
}
