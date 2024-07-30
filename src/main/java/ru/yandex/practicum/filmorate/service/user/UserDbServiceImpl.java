package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Qualifier("UserDbServiceImpl")
public class UserDbServiceImpl implements UserService {

    private final UserStorage userStorage;

    private static final String FRIEND_STATUS_CONFIRMED = "confirmed";
    private static final String FRIEND_STATUS_UNCONFIRMED = "unconfirmed";

    public UserDbServiceImpl(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public User createUser(User requestUser) {
        return userStorage.createUser(requestUser);
    }

    public User updateUser(User user) {
        userStorage.getUserById(user.getId());
        return userStorage.updateUser(user);
    }

    public void addFriends(Long userId, Long friendId) {
        log.info("Adding friend {} to user {}", friendId, userId);
        userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        String status = FRIEND_STATUS_UNCONFIRMED;
        if (friend.getFriends().contains(userId)) {
            log.info("User confirmed the friend request");
            status = FRIEND_STATUS_CONFIRMED;
            userStorage.updateFriendsStatus(friendId, userId, status);
        }
        userStorage.addFriends(userId, friendId, status);
    }

    public void deleteFriends(Long userId, Long friendId) {
        log.info("Removing friend {} from user {}", friendId, userId);
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);

        userStorage.updateFriendsStatus(friendId, userId, FRIEND_STATUS_UNCONFIRMED);
        userStorage.deleteFriends(userId, friendId);
    }

    public List<User> getFriends(long id) {
        userStorage.getUserById(id);
        return userStorage.getFriends(id);
    }

    public List<User> getListMutualFriends(Long userId, Long otherUserId) {
        log.info("Getting list for mutual friends {}", otherUserId);
        List<Long> otherUserList = new ArrayList<>(userStorage.getUserById(otherUserId).getFriends());

        return userStorage.getUserById(userId).getFriends().stream()
                .filter(otherUserList::contains)
                .map(userStorage::getUserById).toList();
    }
}