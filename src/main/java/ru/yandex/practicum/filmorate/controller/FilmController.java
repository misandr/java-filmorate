package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.yandex.practicum.filmorate.Constants.DEFAULT_COUNT_POPULAR_FILMS;

@Slf4j
@RestController
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                          FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}", film);

        return filmStorage.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid  @RequestBody Film film) {
        log.info("Изменение фильма {}", film);

        return filmStorage.updateFilm(film);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Получение списка фильмов.");

        return filmStorage.getFilms();
    }

    @GetMapping("/films/{filmId}")
    public Film findById(@PathVariable @Min(1) Integer filmId) {
        log.info("Получение фильма с id {}", filmId);

        return filmStorage.getFilm(filmId);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable @Min(1) Integer id, @Min(1) @PathVariable Integer userId) {
        log.info("Лайк фильма {} от пользователя {}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable @Min(1) Integer id, @PathVariable @Min(1) Integer userId) {
        log.info("Удаление лайка фильма {} пользователем {}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        if(count == null){
            log.info("Запрос самых популярных фильмов. Кол-во не задано.");

            return filmService.getPopularFilms(DEFAULT_COUNT_POPULAR_FILMS);
        }
        log.info("Запрос {} самых популярных фильмов.", count);

        return filmService.getPopularFilms(count);
    }

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        log.info("Получение списка жанров.");

        return filmService.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable @Min(1) Integer id) {
        log.info("Получение жанра с id {}", id);

        return filmService.getGenre(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getMpas() {
        log.info("Получение списка MPA.");

        return filmService.getMpas();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpa(@PathVariable @Min(1) Integer id) {
        log.info("Получение MPA с id {}", id);

        return filmService.getMpa(id);
    }
}

