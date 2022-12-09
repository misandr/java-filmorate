package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
    }

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
        final ValidationException exception = assertThrows(

                ValidationException.class,
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
