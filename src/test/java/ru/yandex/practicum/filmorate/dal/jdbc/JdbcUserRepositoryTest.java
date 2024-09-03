package ru.yandex.practicum.filmorate.dal.jdbc;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import(JdbcUserRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcUserRepository")
class JdbcUserRepositoryTest {
    private static final Long TEST_USER_ID = 1L;
    private final JdbcUserRepository userRepository;

    static User getTestUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setName("user");
        user.setLogin("login");
        user.setEmail("email.test@yandex.ru");
        user.setBirthday(null);
        return user;
    }

    @Test
    @DisplayName("Получение пользователя по id")
    public void get_shouldReturnUser() {
        Optional<User> userOptional = userRepository.get(TEST_USER_ID);

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("friends")
                .isEqualTo(getTestUser());
    }

    @Test
    @DisplayName("Получение всех пользователей")
    public void getAll_shouldReturnAllUsers() {
        List<User> users = userRepository.getAll();

        assertEquals(1, users.size());
        assertThat(users.getFirst())
                .usingRecursiveComparison()
                .ignoringFields("friends")
                .isEqualTo(getTestUser());
    }

    @Test
    @DisplayName("Создание пользователя")
    public void create_shouldCreateUser() {
        User user = new User();
        user.setName("user2");
        user.setLogin("login2");
        user.setEmail("email2@yandex.ru");
        user.setBirthday(null);

        user.setFriends(new HashSet<>());

        User userCreated = userRepository.create(user);
        assertThat(userCreated)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user);
    }

    @Test
    @DisplayName("Обновление пользователя")
    public void update_shouldUpdateUser() {
        User user = getTestUser();
        user.setName("updated name");
        user.setBirthday(LocalDate.of(1998, 11, 24));

        System.out.println(user.getBirthday());

        userRepository.update(user);
        Optional<User> userUpdated = userRepository.get(user.getId());
        assertThat(userUpdated)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("friends")
                .isEqualTo(user);
    }

    @Test
    @DisplayName("Удаление пользователя")
    public void delete_shouldDeleteUser() {
        User user = getTestUser();
        userRepository.delete(user);

        List<User> users = userRepository.getAll();

        assertEquals(0, users.size());
    }

    @Test
    @DisplayName("Добавление друга у пользователя")
    public void addFriend_shouldAddFriend() {
        User friend = new User();
        friend.setName("friend");
        friend.setLogin("loginFriend");
        friend.setEmail("friend@yandex.ru");
        friend.setBirthday(null);
        friend = userRepository.create(friend);

        userRepository.addFriend(friend.getId(), TEST_USER_ID);
        User user = userRepository.get(TEST_USER_ID).orElseThrow(() -> new NotFoundException("User not found"));
        friend = userRepository.get(friend.getId()).orElseThrow(() -> new NotFoundException("User not found"));

        assertEquals(1, user.getFriends().size());
        assertEquals(0, friend.getFriends().size());
    }

    @Test
    @DisplayName("Получение друзей пользователя")
    public void getFriends_shouldReturnUserFriends() {
        User friend = new User();
        friend.setName("friend");
        friend.setLogin("loginFriend");
        friend.setEmail("friend@yandex.ru");
        friend.setBirthday(null);
        friend = userRepository.create(friend);
        userRepository.addFriend(friend.getId(), TEST_USER_ID);
        User user = userRepository.get(TEST_USER_ID).orElseThrow(() -> new NotFoundException("User not found"));
        friend = userRepository.get(friend.getId()).orElseThrow(() -> new NotFoundException("User not found"));

        List<User> userFriends = new ArrayList<>(userRepository.getFriends(user.getId()));

        assertEquals(user.getFriends().size(), userFriends.size());
        assertThat(userFriends.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(friend);
    }

    @Test
    @DisplayName("Удаление друга у пользователя")
    public void deleteFriend_shouldDeleteFriendFromUser() {
        User friend = new User();
        friend.setName("friend");
        friend.setLogin("loginFriend");
        friend.setEmail("friend@yandex.ru");
        friend.setBirthday(null);
        friend = userRepository.create(friend);
        userRepository.addFriend(friend.getId(), TEST_USER_ID);

        userRepository.removeFriend(TEST_USER_ID, friend.getId());
        User user = userRepository.get(TEST_USER_ID).orElseThrow(() -> new NotFoundException("User not found"));

        assertEquals(0, user.getFriends().size());
    }
}