package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

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
        return userStorage.get(userId).orElseThrow(() -> new NotFoundException("User not found"));
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
        User user = userStorage.get(userId).orElseThrow(() -> new NotFoundException("User not found"));
        user.getFriends().add(friendId);

        User newFriend = userStorage.get(friendId).orElseThrow(() -> new NotFoundException("User not found"));
        newFriend.getFriends().add(user.getId());

        userStorage.update(user);
        userStorage.update(newFriend);

        return user;
    }

    @Override
    public User removeFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId).orElseThrow(() -> new NotFoundException("User not found"));
        user.getFriends().remove(friendId);

        User oldFriend = userStorage.get(friendId).orElseThrow(() -> new NotFoundException("User not found"));
        oldFriend.getFriends().remove(user.getId());

        userStorage.update(user);
        userStorage.update(oldFriend);

        return user;
    }

    @Override
    public Set<User> getUserFriends(Long id) {
        User user = userStorage.get(id).orElseThrow(() -> new NotFoundException("User not found"));

        return user.getFriends().stream()
                .map(userStorage::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.get(userId).orElseThrow(() -> new NotFoundException("User not found"));
        User otherUser = userStorage.get(otherId).orElseThrow(() -> new NotFoundException("User not found"));

        return user.getFriends().stream()
                .filter(friend -> !friend.equals(otherId))
                .filter(friend -> otherUser.getFriends().contains(friend))
                .map(userStorage::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
}
