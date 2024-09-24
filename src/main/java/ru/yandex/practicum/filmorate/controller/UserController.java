package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.Update;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class  UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable Long userId) {
        return userService.get(userId);
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Validated(Update.class) @RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping
    public void delete(@Validated(Update.class) @RequestBody User user) {
        userService.delete(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable("id") Long userId,
            @PathVariable("friendId") Long friendId
    ) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable("id") Long userId,
            @PathVariable("friendId") Long friendId
    ) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getUserFriends(@PathVariable("id") Long id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(
            @PathVariable("id") Long userId,
            @PathVariable("otherId") Long otherId
    ) {
        return userService.getCommonFriends(userId, otherId);
    }
}
