package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;

import static ru.yandex.practicum.filmorate.Constants.DEFAULT_COUNT_POPULAR_FILMS;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        filmService = new FilmService(filmStorage);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}", film);

        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid  @RequestBody Film film) {
        log.info("Изменение фильма {}", film);

        return filmStorage.updateFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @GetMapping("/{filmId}")
    public Film findById(@PathVariable Integer filmId) {
        return filmStorage.getFilm(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Лайк фильма {} от пользователя {}", id, userId);

        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Удаление лайка фильма {} пользователем {}", id, userId);

        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        if(count == null){
            return filmService.getPopularFilms(DEFAULT_COUNT_POPULAR_FILMS);
        }
        return filmService.getPopularFilms(count);
    }
}

