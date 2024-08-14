package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements BaseUserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User get(Long userId) {
        return userStorage.getUser(userId);
    }

    @Override
    public User update(User user) {
        return userStorage.update(user);
    }

    @Override
    public void delete(User user) {
        userStorage.delete(user);
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);

        if (user == null) {
            log.warn("User with id={} not found", userId);
            throw new NotFoundException("User with not found");
        }

        user.getFriends().add(friendId);

        User newFriend = userStorage.getUser(friendId);

        if (newFriend == null) {
            log.warn("Friend with id={} not found", friendId);
            throw new NotFoundException("Friend not found");
        }

        newFriend.getFriends().add(user.getId());
        userStorage.update(user);
        userStorage.update(newFriend);

        return user;
    }

    @Override
    public User removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        user.getFriends().remove(friendId);

        User oldFriend = userStorage.getUser(friendId);

        if (oldFriend == null) {
            throw new NotFoundException("Friend not found");
        }

        oldFriend.getFriends().remove(user.getId());

        userStorage.update(user);
        userStorage.update(oldFriend);

        return user;
    }

    @Override
    public Set<User> getUserFriends(Long id) {
        if (userStorage.getUser(id) == null) {
            throw new NotFoundException("User not found");
        }

        return userStorage.getUser(id).getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> getCommonFriends(Long userId, Long otherId) {
        return userStorage.getUser(userId).getFriends().stream()
                .filter(friend -> !friend.equals(otherId))
                .filter(friend -> userStorage.getUser(otherId).getFriends().contains(friend))
                .map(userStorage::getUser)
                .collect(Collectors.toSet());
    }
}
