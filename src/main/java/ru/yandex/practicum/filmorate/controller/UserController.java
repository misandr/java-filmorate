package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(@Qualifier("userDbStorage")UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Добавление нового пользователя {}", user);

        return userStorage.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Изменение пользователя {}", user);

        return userStorage.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получение списка пользователей.");

        return userStorage.getUsers();
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable @Min(1) Integer userId) {
        log.info("Получение пользователя с id {}", userId);

        return userStorage.getUser(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable @Min(1) Integer id, @PathVariable @Min(1) Integer friendId) {
        log.info("Добавление друга {} к пользователю {}", friendId, id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable @Min(1) Integer id, @PathVariable @Min(1) Integer friendId) {
        log.info("Удаление друга {} у пользователя {}", friendId, id);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable @Min(1) Integer id) {
        log.info("Получение друзей пользователя с id {}", id);

        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable @Min(1) Integer id, @PathVariable @Min(1) Integer otherId) {
        log.info("Список друзей пользователя {}, общих с пользователем {} ", id, otherId);

        return userService.getCommonFriends(id, otherId);
    }
}

