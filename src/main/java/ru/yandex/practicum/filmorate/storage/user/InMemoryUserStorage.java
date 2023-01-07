package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Constants;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private int generateId;
    private final Map<Integer, User> users;

    public InMemoryUserStorage(){
        generateId = 1;
        users = new HashMap<>();
    }

    public User addUser(User user) {

        if(user == null){
            log.warn("Запрос пустой!");
            throw new ValidationException("Запрос пустой!");
        }

        //При @Email можно было бы убрать, но не знаю как в тесте тогда проверить.
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

    public User updateUser(User user) {
        log.info("Изменение пользователя {}", user);
        if(users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }else{
            throw new NotFoundException("Нет такого пользователя!");
        }
        return user;
    }

    public List<User> getUsers(){
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Integer id) {
        if(users.containsKey(id)) {
            return users.get(id);
        }else{
            log.warn("Нет такого пользователя!");
            throw new NotFoundException("Нет такого пользователя!");
        }
    }
}
