package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(long id);

    User createUser(User requestUser);

    User updateUser(User user);

    void addFriends(Long userId, Long friendId);

    void deleteFriends(Long userId, Long friendId);

    List<User> getFriends(long id);

    List<User> getListMutualFriends(Long userId, Long otherUserId);

    void deleteUser(long userId);
}