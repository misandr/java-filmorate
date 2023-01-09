package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Slf4j
@Component
public class LikeDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmStorage;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate, FilmDbStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    public void addLike(Integer id, Integer userId) {
        jdbcTemplate.update("INSERT INTO likes(film_id, user_id) values (?, ?)", id, userId);
    }

    public void removeLike(Integer id, Integer userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", id, userId);
    }

    public List<Film> getPopularFilms(int count) {

        String sqlQuery =
                "SELECT f.id, f.name, description, releaseDate, duration, mpa_id, m.name AS mpa_name " +
                        "FROM films as f " +
                        "LEFT OUTER JOIN(" +
                        "SELECT film_id, COUNT(user_id) as total " +
                        "FROM likes " +
                        "GROUP BY film_id " +
                        ") AS l ON f.id = l.film_id " +
                        "INNER JOIN mpas AS m ON f.mpa_id = m.id " +
                        "ORDER BY l.total DESC " +
                        "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, filmStorage::makeFilm, count);
    }
}
