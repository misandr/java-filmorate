package ru.yandex.practicum.filmorate.service.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {
    private UserStorage userStorage;
    private UserService userService;

    @BeforeEach
    void beforeEach() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }
    @Test
    void getCommonFriends() {

        User createdUser1 = createUser(1,"User1", "Name", "mail@ya.ru","2000-12-28");
        userStorage.addUser(createdUser1);

        User createdUser2 = createUser(2,"User2", "Name", "mail@ya.ru","2000-12-28");
        userStorage.addUser(createdUser2);

        User commonFriend = createUser(3,"Common", "Name", "mail@ya.ru","2000-12-28");
        userStorage.addUser(commonFriend);

        createdUser1.addFriend(3);
        createdUser2.addFriend(3);

        List<User> commonFriends = userService.getCommonFriends(1, 2);

        assertEquals(commonFriend, commonFriends.get(0), "Общие друзья не совпадают.");
    }

    User createUser(int id, String login, String name, String email, String birthday) {

        User user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setName(name);
        user.setEmail(email);
        user.setBirthday(birthday);
        return user;
    }
}
