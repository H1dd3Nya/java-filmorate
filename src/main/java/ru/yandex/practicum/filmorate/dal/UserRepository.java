package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    List<User> getAll();

    User create(User user);

    User update(User user);

    void delete(User user);

    Optional<User> get(Long id);

    void addFriend(Long user, Long target);

    void removeFriend(Long user, Long target);

    Set<User> getFriends(Long id);

    Set<User> getCommonFriends(Long user, Long target);
}
