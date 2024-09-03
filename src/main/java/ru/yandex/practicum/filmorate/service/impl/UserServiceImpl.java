package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.InvalidTargetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(@Qualifier("JdbcUserRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User create(User user) {
        return userRepository.create(user);
    }

    @Override
    public User get(Long userId) {
        return userRepository.get(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User update(User user) {
        final User u = userRepository.get(user.getId()).orElseThrow(() -> new NotFoundException("User not found"));

        u.setName(user.getName());
        u.setEmail(user.getEmail());
        u.setLogin(user.getLogin());
        u.setBirthday(user.getBirthday());
        u.setFriends(new HashSet<>(user.getFriends()));

        return userRepository.update(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new InvalidTargetException("User can not be friend to himself");
        }

        userRepository.get(userId).orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.get(friendId).orElseThrow(() -> new NotFoundException("Friend not found"));

        userRepository.addFriend(friendId, userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        userRepository.get(userId).orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.get(friendId).orElseThrow(() -> new NotFoundException("Friend not found"));

        userRepository.removeFriend(userId, friendId);
    }

    @Override
    public Set<User> getUserFriends(Long id) {
        userRepository.get(id).orElseThrow(() -> new NotFoundException("User not found"));

        return userRepository.getFriends(id);
    }

    @Override
    public Set<User> getCommonFriends(Long userId, Long otherId) {
        User user = userRepository.get(userId).orElseThrow(() -> new NotFoundException("User not found"));
        User otherUser = userRepository.get(otherId).orElseThrow(() -> new NotFoundException("User not found"));

        return user.getFriends().stream()
                .filter(friend -> !friend.equals(otherId))
                .filter(friend -> otherUser.getFriends().contains(friend))
                .map(userRepository::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
}
