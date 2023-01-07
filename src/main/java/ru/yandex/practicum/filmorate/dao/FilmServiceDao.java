package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.List;

@Component
public class FilmServiceDao {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmStorage;
    private final UserStorage userStorage;

    public FilmServiceDao(FilmDbStorage filmStorage,
                          @Qualifier("userDbStorage") UserStorage userStorage,
                          JdbcTemplate jdbcTemplate){
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLike(Integer id, Integer userId){
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);
        if((user != null) && (film != null)) {
            jdbcTemplate.update("INSERT INTO likes(film_id, user_id) values (?, ?)", id, userId);
        }else{
            throw new NotFoundException("Не найден id!");
        }
    }

    public void removeLike(Integer id, Integer userId){
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);
        if((user != null) && (film != null)) {
            jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", id, userId);
        }else{
            throw new NotFoundException("Не найден id!");
        }
    }

    public List<Film> getPopularFilms(int count) {

        String sqlQuery =
                "SELECT id, name, description, releaseDate, duration, mpa_id " +
                "FROM films as f " +
                "LEFT OUTER JOIN(" +
                        "SELECT film_id, COUNT(user_id) as total " +
                        "FROM likes " +
                        "GROUP BY film_id " +
                        ") AS l ON f.id = l.film_id " +

                "ORDER BY l.total DESC "+
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, filmStorage::makeFilm, count);
    }
}
