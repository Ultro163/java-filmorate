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
@Qualifier("UserInMemoryServiceImpl")
public class UserInMemoryServiceImpl implements UserService {
    UserStorage userStorage;

    public UserInMemoryServiceImpl(@Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    @Override
    public User createUser(User requestUser) {
        return userStorage.createUser(requestUser);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    @Override
    public void addFriends(Long userId, Long friendId) {
        userStorage.addFriends(userId, friendId, "ok");
    }

    @Override
    public void deleteFriends(Long userId, Long friendId) {
        userStorage.deleteFriends(userId, friendId);
    }

    @Override
    public List<User> getFriends(long id) {
        return userStorage.getFriends(id);
    }

    @Override
    public List<User> getListMutualFriends(Long userId, Long otherUserId) {
        log.info("Getting list for mutual friends {}", otherUserId);
        List<Long> otherUserList = new ArrayList<>(userStorage.getUserById(otherUserId).getFriends());

        return userStorage.getUserById(userId).getFriends().stream()
                .filter(otherUserList::contains)
                .map(userStorage::getUserById).toList();
    }
}