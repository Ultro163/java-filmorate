package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDbServiceImpl implements UserService {

    private final UserStorage userDbStorage;

    private static final String FRIEND_STATUS_CONFIRMED = "confirmed";
    private static final String FRIEND_STATUS_UNCONFIRMED = "unconfirmed";

    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public User getUserById(long id) {
        return userDbStorage.getUserById(id);
    }

    public User createUser(User requestUser) {
        return userDbStorage.createUser(requestUser);
    }

    public User updateUser(User user) {
        userDbStorage.getUserById(user.getId());
        return userDbStorage.updateUser(user);
    }

    public void addFriends(Long userId, Long friendId) {
        log.info("Adding friend {} to user {}", friendId, userId);
        userDbStorage.getUserById(userId);
        User friend = userDbStorage.getUserById(friendId);

        String status = FRIEND_STATUS_UNCONFIRMED;
        if (friend.getFriends().contains(userId)) {
            log.info("User confirmed the friend request");
            status = FRIEND_STATUS_CONFIRMED;
            userDbStorage.updateFriendsStatus(friendId, userId, status);
        }
        userDbStorage.addFriends(userId, friendId, status);
    }

    public void deleteFriends(Long userId, Long friendId) {
        log.info("Removing friend {} from user {}", friendId, userId);
        userDbStorage.getUserById(userId);
        userDbStorage.getUserById(friendId);

        userDbStorage.updateFriendsStatus(friendId, userId, FRIEND_STATUS_UNCONFIRMED);
        userDbStorage.deleteFriends(userId, friendId);
    }

    public List<User> getFriends(long id) {
        userDbStorage.getUserById(id);
        return userDbStorage.getFriends(id);
    }

    public List<User> getListMutualFriends(Long userId, Long otherUserId) {
        log.info("Getting list for mutual friends {}", otherUserId);
        List<Long> otherUserList = new ArrayList<>(userDbStorage.getUserById(otherUserId).getFriends());

        return userDbStorage.getUserById(userId).getFriends().stream()
                .filter(otherUserList::contains)
                .map(userDbStorage::getUserById).toList();
    }
}