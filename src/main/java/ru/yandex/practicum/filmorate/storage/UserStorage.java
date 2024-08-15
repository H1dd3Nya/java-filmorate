package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAll();

    User create(User user);

    User update(User user);

    void delete(User user);

    Optional<User> get(Long id);
}
