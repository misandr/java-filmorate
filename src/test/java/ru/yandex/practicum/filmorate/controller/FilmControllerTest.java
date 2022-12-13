package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController(new InMemoryFilmStorage());
    }
    @Test
    void addFilm() {
        Film createdFilm = createFilm(1,"Фильм 1", "Комедия", "1895-12-28", 60);

        Film film = filmController.addFilm(createdFilm);

        assertEquals(film, createdFilm, "Фильмы не совпадают");
    }

    @Test
    void updateFilm() {
        Film createdFilm = createFilm(1,"Фильм 1", "Комедия", "1895-12-28", 60);

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
    void shouldThrowExceptionWhenEmptyName() {
        final ValidationException exception = assertThrows(

                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.addFilm(createFilm(1,
                                "", "Комедия", "2001-01-11", 60));
                    }
                });
        assertEquals("Не указано название фильма!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenBigDescription() {
        final ValidationException exception = assertThrows(

                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        String description = "";
                        for(int i = 0; i < 201; i++){
                            description += "a";
                        }
                        filmController.addFilm(createFilm(1,
                                "Фильм 1", description, "2001-01-11", 60));
                    }
                });
        assertEquals("Описание фильма превышает 200 символов!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNotValidDate() {
        final ValidationException exception = assertThrows(

                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.addFilm(createFilm(1,
                                "Фильм 1", "Комедия", "1894-01-11", 60));
                    }
                });
        assertEquals("Фильмы ещё не придумали!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNotValidDuration() {
        final ValidationException exception = assertThrows(

                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.addFilm(createFilm(1,
                                "Фильм 1", "Комедия", "1896-01-11", -1));
                    }
                });
        assertEquals("Длительность фильма отрицательная!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdateUnknownFilm() {
        final NotFoundException exception = assertThrows(

                NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        Film createdFilm = createFilm(1,"Фильм 1", "Комедия", "1995-12-28", 60);
                        filmController.addFilm(createdFilm);

                        createdFilm.setId(100);
                        filmController.updateFilm(createdFilm);
                    }
                });
        assertEquals("Нет такого фильма!", exception.getMessage());
    }

    Film createFilm(int id, String name, String description, String releaseDate, int duration) {

        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        return film;
    }
}
