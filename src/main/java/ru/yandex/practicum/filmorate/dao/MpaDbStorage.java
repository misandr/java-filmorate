package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getById(Integer id){
        String sqlQuery = "SELECT id, name FROM mpas WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeMpa, id);
        }catch(DataAccessException exception) {
            log.warn("Нет такого MPA!");
            throw new NotFoundException("Нет такого MPA!");
        }
    }

    public List<Mpa> getAll(){
        return jdbcTemplate.query("SELECT id, name FROM mpas", this::makeMpa);
    }

    private Mpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Integer id = resultSet.getInt("id");
        String name = resultSet.getString("name");

        Mpa mpa = new Mpa(id, name);

        return mpa;
    }
}