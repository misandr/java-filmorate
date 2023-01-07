package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    private final UserController userController;

    @Test
    void addUser() {
        User createdUser = createUser(1,"User", "Name", "mail@ya.ru","2000-12-28");

        User user = userController.addUser(createdUser);

        assertEquals(user, createdUser, "Пользователи не совпадают");
    }

    @Test
    void updateUser() {
        User createdUser = createUser(1,"User", "Name", "mail@ya.ru","2000-12-28");

        userController.addUser(createdUser);

        createdUser.setBirthday("2000-12-29");

        User user = userController.addUser(createdUser);

        assertEquals(user, createdUser, "Пользователи не совпадают");
    }

    @Test
    void shouldThrowExceptionWhenNullUser() {
        final ValidationException exception = assertThrows(

                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.addUser(null);
                    }
                });
        assertEquals("Запрос пустой!", exception.getMessage());
    }

    @Test
    void addUserWithEmptyName() {
        User createdUser = createUser(1,"User", "", "mail@ya.ru","2000-12-28");

        User user = userController.addUser(createdUser);

        assertEquals(user.getName(), createdUser.getLogin(), "Имя не равно логину");
    }

    @Test
    void addUserWithNullName() {
        User createdUser = createUser(1,"User", null, "mail@ya.ru","2000-12-28");

        User user = userController.addUser(createdUser);

        assertEquals(user.getName(), createdUser.getLogin(), "Имя не равно логину");
    }

    @Test
    void shouldThrowExceptionWhenEmptyEmail() {
        final ValidationException exception = assertThrows(

                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.addUser(createUser(1,
                                "User", "Name", "","2000-12-28"));
                    }
                });
        assertEquals("Почта неправильная!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenBadEmail() {
        final ValidationException exception = assertThrows(

                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.addUser(createUser(1,
                                "User", "Name", "mail ya.ru","2000-12-28"));
                    }
                });
        assertEquals("Почта неправильная!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmptyLogin() {
        final ValidationException exception = assertThrows(

                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        String description = "";
                        for(int i = 0; i < 201; i++){
                            description += "a";
                        }
                        userController.addUser(createUser(1,
                                "", "Name", "mail@ya.ru","2000-12-28"));
                    }
                });
        assertEquals("Логин неправильный!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLoginWithSpace() {
        final ValidationException exception = assertThrows(

                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        String description = "";
                        for(int i = 0; i < 201; i++){
                            description += "a";
                        }
                        userController.addUser(createUser(1,
                                "User 1", "Name", "mail@ya.ru","2000-12-28"));
                    }
                });
        assertEquals("Логин неправильный!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNotValidBirthday() {
        final ValidationException exception = assertThrows(

                ValidationException.class,

                new Executable() {
                    @Override
                    public void execute() {
                        userController.addUser(createUser(1,
                                "User", "Name", "mail@ya.ru","2023-12-28"));
                    }
                });
        assertEquals("Дата рождения в будущем!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdateUnknownUser() {
        final NotFoundException exception = assertThrows(

                NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        User createdUser = createUser(1,"User", "Name", "mail@ya.ru","2000-12-28");
                        userController.addUser(createdUser);

                        createdUser.setId(100);
                        userController.updateUser(createdUser);
                    }
                });
        assertEquals("Нет такого пользователя!", exception.getMessage());
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
