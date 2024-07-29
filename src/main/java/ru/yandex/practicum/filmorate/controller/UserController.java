package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long counter = 1L;

    @GetMapping
    public List<User> getAll() {
        log.info("Started collecting users");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Started creating user");
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(counter);
        users.put(user.getId(), user);
        counter++;

        log.info("User successfully created");
        return user;
    }

    @PutMapping
    public User update(@Validated(Update.class) @RequestBody User user) {
        log.info("Started updating user");
        User oldUser = users.get(user.getId());

        if (oldUser == null) {
            log.warn("User with id: {} does not exist", user.getId());
            throw new NotFoundException("User not found");
        }

        oldUser.setName(user.getName());
        oldUser.setLogin(user.getLogin());
        oldUser.setEmail(user.getEmail());
        oldUser.setBirthday(user.getBirthday());

        log.info("User successfully updated");
        return oldUser;
    }
}
