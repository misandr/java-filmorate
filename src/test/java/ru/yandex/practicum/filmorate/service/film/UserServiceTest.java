package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserDbStorage userStorage;
    private final UserService userService;

    @Test
    void getCommonFriends() {

        User createdUser1 = createUser("User1", "Name", "mail@ya.ru","2000-12-28");
        User createdUser2 = createUser("User2", "Name", "mail@ya.ru","2000-12-28");

        User commonFriend = createUser("Common", "Name", "mail@ya.ru","2000-12-28");

        userService.addFriend(createdUser1.getId(), commonFriend.getId());
        userService.addFriend(createdUser2.getId(), commonFriend.getId());

        List<User> commonFriends = userService.getCommonFriends(createdUser1.getId(), createdUser2.getId());

        assertEquals(commonFriend, commonFriends.get(0), "Общие друзья не совпадают.");
    }

    User createUser(String login, String name, String email, String birthday) {

        User user = new User();
        user.setId(0);
        user.setLogin(login);
        user.setName(name);
        user.setEmail(email);
        user.setBirthday(birthday);

        return userStorage.addUser(user);
    }
}
