package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {
    private final FilmController filmController;

    private static ValidatorFactory factory;
    private static Validator validator;
    @BeforeAll
    static void beforeAll(){
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void addFilm() {
        Film createdFilm = createFilm(1,"Фильм 1", "Комедия", "1895-12-28",
                60, 1);

        Film film = filmController.addFilm(createdFilm);

        assertEquals(film, createdFilm, "Фильмы не совпадают");
    }

    @Test
    void updateFilm() {
        Film createdFilm = createFilm(1,"Фильм 1", "Комедия", "1895-12-28",
                60, 1);

        filmController.addFilm(createdFilm);

        createdFilm.setName("Фильм 1");

        Film film = filmController.updateFilm(createdFilm);

        assertEquals(film, createdFilm, "Фильмы не совпадают");
    }

    @Test
    void shouldThrowExceptionWhenNullFilm() {
        final ValidationException exception = assertThrows(

                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.addFilm(null);
                    }
                });
        assertEquals("Запрос пустой!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNotValidDate() {
        final ValidationException exception = assertThrows(

                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.addFilm(createFilm(1,
                                "Фильм 1", "Комедия", "1894-01-11", 60, 1));
                    }
                });
        assertEquals("Фильмы ещё не придумали!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNotValidDuration() {
        Film film = createFilm(1,
                "Фильм 1", "Комедия", "1896-01-11", -1, 1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Длительность фильма отрицательная!");
        violations.clear();
    }

    @Test
    void shouldThrowExceptionWhenUpdateUnknownFilm() {
        final NotFoundException exception = assertThrows(

                NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        Film createdFilm = createFilm(1,"Фильм 1", "Комедия", "1995-12-28",
                                60, 1);
                        filmController.addFilm(createdFilm);

                        createdFilm.setId(100);
                        filmController.updateFilm(createdFilm);
                    }
                });
        assertEquals("Нет такого фильма!", exception.getMessage());
    }

    Film createFilm(int id, String name, String description, String releaseDate, int duration, int mpaId) {

        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setMpa(new Mpa(mpaId));

        return film;
    }
}
