package ru.yandex.practicum.filmorate.dal.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long counter = 1L;

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(counter);
        users.put(user.getId(), user);
        counter++;

        return user;
    }

    public User update(User user) {
        User oldUser = users.get(user.getId());

        if (oldUser == null) {
            log.warn("User with id: {} does not exist", user.getId());
            throw new NotFoundException("User not found");
        }

        oldUser.setName(user.getName());
        oldUser.setLogin(user.getLogin());
        oldUser.setEmail(user.getEmail());
        oldUser.setBirthday(user.getBirthday());

        return oldUser;
    }

    public void delete(User user) {
        users.remove(user.getId());
    }

    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public void addFriend(Long user, Long target) {
        users.get(target).getFriends().add(user);
    }

    public void removeFriend(Long user, Long target) {
        users.get(target).getFriends().remove(user);
    }

    public Set<User> getFriends(Long id) {
        return users.get(id).getFriends()
                .stream()
                .map(this::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(Long user, Long target) {
        return users.get(user).getFriends().stream()
                .filter(friend -> !friend.equals(target))
                .filter(friend -> users.get(target).getFriends().contains(friend))
                .map(this::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
}
