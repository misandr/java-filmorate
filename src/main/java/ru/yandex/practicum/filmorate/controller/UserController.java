package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int generateId;
    private final Map<Integer, User> users;

    public UserController() {
        generateId = 1;
        users = new HashMap<>();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Добавление нового пользователя {}", user);

        if(user == null){
            log.warn("Запрос пустой!");
            throw new ValidationException("Запрос пустой!");
        }

        if(user.getEmail().isBlank() || !user.getEmail().contains("@")){
            log.warn("Почта неправильная!");
            throw new ValidationException("Почта неправильная!");
        }

        if(user.getLogin().isBlank() || user.getLogin().contains(" ")){
            log.warn("Логин неправильный!");
            throw new ValidationException("Логин неправильный!");
        }

        if((user.getName() == null) || user.getName().isBlank()){
            log.warn("Логин скопирован в имя!");
            user.setName(user.getLogin());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN);
        LocalDate dateFilm = LocalDate.parse(user.getBirthday(), formatter);

        if(dateFilm.isAfter(LocalDate.now())){
            log.warn("Дата рождения в будущем!");
            throw new ValidationException("Дата рождения в будущем!");
        }

        user.setId(generateId);
        users.put(generateId, user);

        generateId++;
        System.out.println(user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Изменение пользователя {}", user);
        if(users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }else{
            throw new ValidationException("Нет такого пользователя!");
        }
        return user;
    }

    @GetMapping
    public String listUsers() {
        Gson gson = new Gson();
        System.out.println(gson.toJson(users.values()));
        return gson.toJson(users.values());
    }
}

