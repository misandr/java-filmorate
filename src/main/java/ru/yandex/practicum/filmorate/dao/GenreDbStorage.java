package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getById(Integer id){
        String sqlQuery = "SELECT id, name FROM genres WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeGenre, id);
        }catch(DataAccessException exception) {
            log.warn("Нет такого жанра!");
            throw new NotFoundException("Нет такого жанра!");
        }
    }

    public List<Genre> getAll(){
        return jdbcTemplate.query("SELECT id, name FROM genres", this::makeGenre);
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {

        Integer id = resultSet.getInt("id");
        String name = resultSet.getString("name");

        Genre genre = new Genre();
        genre.setId(id);
        genre.setName(name);

        return genre;
    }
}