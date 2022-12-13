package ru.yandex.practicum.filmorate.service.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {
    private FilmStorage filmStorage;
    private FilmService filmService;

    @BeforeEach
    void beforeEach() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage);
    }
    @Test
    void getPopularFilms() {

        Film[] films = new Film[15];

        films[0] = createFilmWithLikes("Фильм1", "Комедия", "1895-12-28", 60, 2);
        films[1] = createFilmWithLikes("Фильм2", "Комедия", "1895-12-28", 60, 5);
        films[2] = createFilmWithLikes("Фильм3", "Комедия", "1895-12-28", 6,12);
        films[3] = createFilmWithLikes("Фильм4", "Комедия", "1895-12-28", 60, 2);
        films[4] = createFilmWithLikes("Фильм5", "Комедия", "1895-12-28", 60, 11);
        films[5] = createFilmWithLikes("Фильм6", "Комедия", "1895-12-28", 6,0);
        films[6] = createFilmWithLikes("Фильм7", "Комедия", "1895-12-28", 60, 5);
        films[7] = createFilmWithLikes("Фильм8", "Комедия", "1895-12-28", 60, 10);
        films[8] = createFilmWithLikes("Фильм9", "Комедия", "1895-12-28", 6,6);
        films[9] = createFilmWithLikes("Фильм10", "Комедия", "1895-12-28", 60, 9);
        films[10] = createFilmWithLikes("Фильм1", "Комедия", "1895-12-28", 60, 3);
        films[11] = createFilmWithLikes("Фильм12", "Комедия", "1895-12-28", 6,8);
        films[12] = createFilmWithLikes("Фильм13", "Комедия", "1895-12-28", 60, 6);
        films[13] = createFilmWithLikes("Фильм14", "Комедия", "1895-12-28", 60, 1);
        films[14] = createFilmWithLikes("Фильм15", "Комедия", "1895-12-28", 6,13);

        for(int i = 0; i < films.length; i++) {
            filmStorage.addFilm(films[i]);
        }

        List<Film> popularFilms = filmService.getPopularFilms(6);
        assertArrayEquals(popularFilms.toArray(),
                new Film[]{films[14],films[2],films[4],films[7], films[9], films[11]}, "Фильмы не совпадают");
    }

    Film createFilm(String name, String description, String releaseDate, int duration) {

        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        return film;
    }

    Film createFilmWithLikes(String name, String description, String releaseDate, int duration, int countLikes) {

        Film film = createFilm(name, description, releaseDate, duration);
        for(int i = 1; i <= countLikes; i ++) {
            film.addLike(i);
        }
        return film;
    }
}
