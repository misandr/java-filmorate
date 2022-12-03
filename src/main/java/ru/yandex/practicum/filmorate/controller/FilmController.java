package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int generateId;
    private final Map<Integer, Film> films;

    public FilmController() {
        generateId = 1;
        films = new HashMap<>();
    }

    // Как @Valid действует я примерно понял. Можно убрать лишние проверки в функциях.
    // Но я не понял, как это можно было бы учесть в Unit-тестах. Или пока Unit-тесты не нужны?

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}", film);

        if(film == null){
            log.warn("Запрос пустой!");
            throw new ValidationException("Запрос пустой!");
        }

        if(film.getName().isBlank()){
            log.warn("При добавлении фильма не указано название!");
            throw new ValidationException("Не указано название фильма!");
        }

        if(film.getDescription().length() >= 200){
            log.warn("Описание фильма превышает 200 символов!");
            throw new ValidationException("Описание фильма превышает 200 символов!");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN);
        LocalDate dateFilm = LocalDate.parse(film.getReleaseDate(), formatter);

        if(dateFilm.isBefore(LocalDate.of(1895, 12, 28))){
            log.warn("Дата создания фильма ошибочна!");
            throw new ValidationException("Фильмы ещё не придумали!");
        }

        if(film.getDuration()< 0){
            log.warn("Длительность фильма отрицательная!");
            throw new ValidationException("Длительность фильма отрицательная!");
        }

        film.setId(generateId);
        films.put(generateId, film);

        generateId++;

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid  @RequestBody Film film) {
        log.info("Изменение фильма {}", film);

        if(films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }else{
            log.warn("Нет такого фильма!");
            throw new ValidationException("Нет такого фильма!");
        }
        return film;
    }

    @GetMapping
    public String listFilms() {
        Gson gson = new Gson();
        System.out.println(gson.toJson(films.values()));
        return gson.toJson(films.values());
    }


}

