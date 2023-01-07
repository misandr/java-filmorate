package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Constants;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public User addUser(User user) {

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

        String sqlQuery = "INSERT INTO users(name, login, email, birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getBirthday());
            return stmt;
        }, keyHolder);
        int id =  keyHolder.getKey().intValue();
        user.setId(id);

        return user;
    }

    public User updateUser(User user) {
        log.info("Изменение пользователя {}", user);

        String sqlQuery = "UPDATE users set " +
                "name = ?, login = ?, email = ?, birthday = ? " +
                "WHERE id = ?";
        if(jdbcTemplate.update(sqlQuery
                    , user.getName()
                    , user.getLogin()
                    , user.getEmail()
                    , user.getBirthday()
                    , user.getId()) == 0){
            throw new NotFoundException("Нет такого пользователя!");
        }
        return user;
    }

    public List<User> getUsers(){
        return jdbcTemplate.query("SELECT id, name, login, email, birthday FROM users", this::makeUser);
    }

    @Override
    public User getUser(Integer id) {

        String sqlQuery = "SELECT id, name, login, email, birthday " +
                "FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
        }catch(DataAccessException exception) {
            log.warn("Нет такого пользователя!");
            throw new NotFoundException("Нет такого пользователя!");
        }
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {

        Integer id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String login = resultSet.getString("login");
        String email = resultSet.getString("email");
        String birthday = resultSet.getString("birthday");

        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthday);

        return user;
    }
}
