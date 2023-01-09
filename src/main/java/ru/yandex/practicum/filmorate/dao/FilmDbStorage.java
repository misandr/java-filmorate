package ru.yandex.practicum.filmorate.dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.Constants;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.TableGenre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final Map<Integer, String> genreTables;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final List<Genre> genres;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;

        genreTables = new HashMap<>();
        // Да, попытался закэшировать)
        String sqlQuery = "SELECT * FROM genres";
        List<TableGenre> nameTables = jdbcTemplate.query(sqlQuery, this::makeTableGenre);

        for(TableGenre table: nameTables){
            genreTables.put(table.getId(), table.getTableName());
        }

        genres = genreDbStorage.getAll();
    }

    @Override
    public Film addFilm(Film film) {
        if(film == null){
            log.warn("Запрос пустой!");
            throw new ValidationException("Запрос пустой!");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN);
        LocalDate dateFilm = LocalDate.parse(film.getReleaseDate(), formatter);

        if(dateFilm.isBefore(LocalDate.of(1895, 12, 28))){
            log.warn("Дата создания фильма ошибочна!");
            throw new ValidationException("Фильмы ещё не придумали!");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            String sqlQuery = "INSERT INTO films(name, description, releaseDate, duration, mpa_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setString(3, film.getReleaseDate());
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        int id =  keyHolder.getKey().intValue();
        film.setId(id);

        Mpa mpa = mpaDbStorage.getById(film.getMpa().getId());
        film.setMpa(mpa);

        if(film.getGenres() != null) {
            for (Genre genre: film.getGenres()) {
                Genre genreDB = genreDbStorage.getById(genre.getId());
                genre.setName(genreDB.getName());
                String genreTable = genreTables.get(genre.getId());
                String sqlQuery = "INSERT INTO " + genreTable +"(film_id) values (?)";
                jdbcTemplate.update(sqlQuery, id);
            }
        }

        return film;
    }

    @Override
    public Film updateFilm(@Valid @RequestBody Film film) {
        String sqlQuery = "UPDATE films set " +
                "name = ?, description = ?, releaseDate = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";
        if(jdbcTemplate.update(sqlQuery
                    , film.getName()
                    , film.getDescription()
                    , film.getReleaseDate()
                    , film.getDuration()
                    , film.getMpa().getId()
                    , film.getId()) == 0){
            log.warn("Нет такого фильма!");
            throw new NotFoundException("Нет такого фильма!");
        }

        Mpa mpa = mpaDbStorage.getById(film.getMpa().getId());
        film.setMpa(mpa);

        if(film.getGenres() != null){
            for(Genre genre: genres){
                Genre findedGenre = film.getGenre(genre.getId());
                String genreTable = genreTables.get(genre.getId());
                if(findedGenre != null){
                    sqlQuery = "SELECT * FROM " + genreTable + " WHERE film_id = ?";
                    if(!jdbcTemplate.queryForRowSet(sqlQuery, film.getId()).next()){
                        sqlQuery = "INSERT INTO " + genreTable + "(film_id) VALUES (?)";
                        jdbcTemplate.update(sqlQuery, film.getId());
                    }
                    Genre genreDB = genreDbStorage.getById(genre.getId());
                    findedGenre.setName(genreDB.getName());
                }else{
                    sqlQuery = "SELECT * FROM " + genreTable + " WHERE film_id = ?";
                    if(jdbcTemplate.queryForRowSet(sqlQuery, film.getId()).next()){
                        sqlQuery = "DELETE FROM " + genreTable + " WHERE film_id = ?";
                        jdbcTemplate.update(sqlQuery, film.getId());
                    }
                }
            }
        }else{
            for(Genre genre: genres){
                String genreTable = genreTables.get(genre.getId());

                sqlQuery = "SELECT * FROM " + genreTable + " WHERE film_id = ?";
                if(jdbcTemplate.queryForRowSet(sqlQuery, film.getId()).next()){
                    sqlQuery = "DELETE FROM " + genreTable + " WHERE film_id = ?";
                    jdbcTemplate.update(sqlQuery, film.getId());
                }
            }
        }

        film.sortGenres();
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT f.id, f.name, description, releaseDate, duration, mpa_id, m.name AS mpa_name " +
                "FROM films as f " +
                "INNER JOIN mpas AS m ON f.mpa_id = m.id ";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public Film getFilm(Integer id) {
        String sqlQuery = "SELECT f.id, f.name, description, releaseDate, duration, mpa_id, m.name AS mpa_name " +
                "FROM films as f " +
                "INNER JOIN mpas AS m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
        }catch(DataAccessException exception) {
            log.warn("Нет такого фильма!");
            throw new NotFoundException("Нет такого фильма!");
        }
    }

    public Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {

        Integer id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        String releaseDate = resultSet.getString("releaseDate");
        int duration = resultSet.getInt("duration");
        int mpaId = resultSet.getInt("mpa_id");
        String mpaName = resultSet.getString("mpa_name");

        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setMpa(new Mpa(mpaId, mpaName));

        for(Genre genre: genres){
            String sqlQuery = "SELECT * FROM " + genreTables.get(genre.getId()) +" WHERE film_id = ?";
            if(jdbcTemplate.queryForRowSet(sqlQuery, film.getId()).next()){
                film.addGenre(genreDbStorage.getById(genre.getId()));
            }
        }

        return film;
    }

    private TableGenre makeTableGenre(ResultSet resultSet, int rowNum) throws SQLException {

        Integer id = resultSet.getInt("id");
        String name = resultSet.getString("table_name");

        TableGenre tableGenre = new TableGenre();
        tableGenre.setId(id);
        tableGenre.setTableName(name);

        return tableGenre;
    }
}
