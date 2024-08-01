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
public class UserInMemoryServiceImpl implements UserService {
    private final UserStorage inMemoryUserStorage;

    @Override
    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    @Override
    public User getUserById(long id) {
        return inMemoryUserStorage.getUserById(id);
    }

    @Override
    public User createUser(User requestUser) {
        return inMemoryUserStorage.createUser(requestUser);
    }

    @Override
    public User updateUser(User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    @Override
    public void addFriends(Long userId, Long friendId) {
        inMemoryUserStorage.addFriends(userId, friendId, "ok");
    }

    @Override
    public void deleteFriends(Long userId, Long friendId) {
        inMemoryUserStorage.deleteFriends(userId, friendId);
    }

    @Override
    public List<User> getFriends(long id) {
        return inMemoryUserStorage.getFriends(id);
    }

    @Override
    public List<User> getListMutualFriends(Long userId, Long otherUserId) {
        log.info("Getting list for mutual friends {}", otherUserId);
        List<Long> otherUserList = new ArrayList<>(inMemoryUserStorage.getUserById(otherUserId).getFriends());

        return inMemoryUserStorage.getUserById(userId).getFriends().stream()
                .filter(otherUserList::contains)
                .map(inMemoryUserStorage::getUserById).toList();
    }
}