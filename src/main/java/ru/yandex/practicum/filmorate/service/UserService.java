package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> getAll();

    User create(User user);

    User get(Long userId);

    User update(User user);

    void delete(User user);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Set<User> getUserFriends(Long id);

    Set<User> getCommonFriends(Long userId, Long otherId);
}
