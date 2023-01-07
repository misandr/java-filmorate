package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Component
public class UserServiceDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public UserServiceDao(@Qualifier("userDbStorage") UserStorage userStorage,
                          JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    public void addFriend(Integer id, Integer friendId){
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        if((user != null) && (friend != null)) {
            String sqlQuery = "INSERT INTO friends(user_id_from, user_id_to, accepted) " +
                    "values (?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setInt(1, id);
                stmt.setInt(2, friendId);
                stmt.setString(3, "FALSE");
                return stmt;
            }, keyHolder);
        }else{
            throw new NotFoundException("Не найден id!");
        }
    }

    public void removeFriend(Integer id, Integer friendId){

        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        if((user != null) && (friend != null)) {
            String sqlQuery = "DELETE FROM friends where user_id_from = ? AND user_id_to = ?";
            jdbcTemplate.update(sqlQuery, id, friendId);
        }else{
            throw new NotFoundException("Не найден id!");
        }
    }

    public List<User> getFriends(Integer id) {
        String sqlQuery =
                "SELECT id, name, login, email, birthday " +
                "FROM users " +
                "WHERE id IN (" +

                "SELECT f.user_id_to " +
                "FROM friends AS f " +
                "WHERE f.user_id_from = ? " +

                "UNION ALL " +

                "SELECT f.user_id_from " +
                "FROM friends AS f " +
                "WHERE f.user_id_to = ? AND f.accepted)";

        System.out.println(jdbcTemplate.query(sqlQuery, this::makeUser, id, id));

        return jdbcTemplate.query(sqlQuery, this::makeUser, id, id);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId){
        ArrayList<User> commonFriends = new ArrayList<>();

        List<User> friendsUser1 = getFriends(id);
        List<User> friendsUser2 = getFriends(otherId);

        if((friendsUser1 != null) && (friendsUser2 != null)) {
            for (User friendId : friendsUser1) {
                if (friendsUser2.contains(friendId)) {
                    commonFriends.add(friendId);
                }
            }
        }

        return commonFriends;
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