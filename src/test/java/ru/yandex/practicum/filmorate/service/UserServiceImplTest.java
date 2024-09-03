package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dal.mem.InMemoryUserRepository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceImplTest {

    private final UserServiceImpl userServiceImpl = new UserServiceImpl(new InMemoryUserRepository());

    @Test
    @DisplayName("Создание пользователя")
    public void create_ShouldCreateNewUser() {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test123");
        user.setEmail("test@123.com");
        user.setBirthday(LocalDate.of(1990, 11, 4));

        userServiceImpl.create(user);

        assertEquals(1, userServiceImpl.getAll().size());
        assertEqualsUsers(user, userServiceImpl.get(1L));
    }

    @Test
    @DisplayName("Получение пользователя")
    public void get_ShouldReturnUser() {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test123");
        user.setEmail("test@123.com");
        user.setBirthday(LocalDate.of(1990, 11, 4));
        userServiceImpl.create(user);

        User userFromService = userServiceImpl.get(1L);

        assertEqualsUsers(user, userFromService);
    }

    @Test
    @DisplayName("Обновление пользователя")
    public void update_ShouldUpdateUser() {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test123");
        user.setEmail("test@123.com");
        user.setBirthday(LocalDate.of(1990, 11, 4));
        user = userServiceImpl.create(user);

        user.setEmail("test2@gmail.com");
        user.setLogin("test0vich111");
        User userFromService = userServiceImpl.update(user);

        assertEqualsUsers(user, userFromService);
    }

    @Test
    @DisplayName("Удаление пользователя")
    public void delete_ShouldDeleteUser() {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test123");
        user.setEmail("test@123.com");
        user.setBirthday(LocalDate.of(1990, 11, 4));
        userServiceImpl.create(user);

        userServiceImpl.delete(user);

        assertEquals(0, userServiceImpl.getAll().size());
    }

    @Test
    @DisplayName("Добавление друга")
    public void addFriend_ShouldAddFriend() {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test123");
        user.setEmail("test@123.com");
        user.setBirthday(LocalDate.of(1990, 11, 4));
        User user2 = new User();
        user2.setName("Test2");
        user2.setLogin("Test456");
        user2.setEmail("test@456.com");
        user2.setBirthday(LocalDate.of(1990, 11, 4));
        userServiceImpl.create(user);
        userServiceImpl.create(user2);

        userServiceImpl.addFriend(user.getId(), user2.getId());

        assertEquals(1, userServiceImpl.getUserFriends(user2.getId()).size());
    }

    @Test
    @DisplayName("Удаление друга")
    public void removeFriend_ShouldRemoveFriend() {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test123");
        user.setEmail("test@123.com");
        user.setBirthday(LocalDate.of(1990, 11, 4));
        User user2 = new User();
        user2.setName("Test2");
        user2.setLogin("Test456");
        user2.setEmail("test@456.com");
        user2.setBirthday(LocalDate.of(1990, 11, 4));
        userServiceImpl.create(user);
        userServiceImpl.create(user2);
        userServiceImpl.addFriend(user.getId(), user2.getId());

        userServiceImpl.removeFriend(user.getId(), user2.getId());

        assertEquals(0, userServiceImpl.getUserFriends(user2.getId()).size());
    }

    @Test
    @DisplayName("Получение списка друзей")
    public void getUserFriends_ShouldReturnUserFriendsList() {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test123");
        user.setEmail("test@123.com");
        user.setBirthday(LocalDate.of(1990, 11, 4));
        User user2 = new User();
        user2.setName("Test2");
        user2.setLogin("Test456");
        user2.setEmail("test@456.com");
        user2.setBirthday(LocalDate.of(1990, 11, 4));
        userServiceImpl.create(user);
        userServiceImpl.create(user2);
        userServiceImpl.addFriend(user.getId(), user2.getId());

        List<User> userFriends = new ArrayList<>(userServiceImpl.getUserFriends(user2.getId()));

        assertEquals(1, userFriends.size());
    }

    @Test
    @DisplayName("Получение общих друзей")
    public void getCommonFriends() {
        User user = new User();
        user.setName("Test");
        user.setLogin("Test123");
        user.setEmail("test@123.com");
        user.setBirthday(LocalDate.of(1990, 11, 4));
        User user2 = new User();
        user2.setName("Test2");
        user2.setLogin("Test456");
        user2.setEmail("test@456.com");
        user2.setBirthday(LocalDate.of(1990, 11, 4));
        User user3 = new User();
        user3.setName("Test3");
        user3.setLogin("Test789");
        user3.setEmail("test@789.com");
        user3.setBirthday(LocalDate.of(1992, 8, 14));
        userServiceImpl.create(user);
        userServiceImpl.create(user2);
        userServiceImpl.create(user3);
        userServiceImpl.addFriend(user2.getId(), user.getId());
        userServiceImpl.addFriend(user.getId(), user2.getId());
        userServiceImpl.addFriend(user3.getId(), user.getId());
        userServiceImpl.addFriend(user.getId(), user3.getId());

        List<User> userCommonFriends = new ArrayList<>(userServiceImpl.getCommonFriends(user2.getId(), user3.getId()));

        assertEquals(1, userCommonFriends.size());
        assertEqualsUsers(user, userCommonFriends.getFirst());
    }

    private void assertEqualsUsers(User expected, User actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getLogin(), actual.getLogin());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getBirthday(), actual.getBirthday());
    }
}