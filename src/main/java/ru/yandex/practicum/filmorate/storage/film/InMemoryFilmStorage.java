package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.Constants;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int generateId;
    private final Map<Integer, Film> films;

    public InMemoryFilmStorage() {
        generateId = 1;
        films = new HashMap<>();
    }

    @Override
    public Film addFilm(Film film) {
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

        //При @PositiveOrZero можно было бы убрать, но не знаю как в тесте тогда проверить.
        if(film.getDuration()< 0){
            log.warn("Длительность фильма отрицательная!");
            throw new ValidationException("Длительность фильма отрицательная!");
        }

        film.setId(generateId);
        films.put(generateId, film);

        generateId++;

        return film;
    }

    @Override
    public Film updateFilm(@Valid  @RequestBody Film film) {
        log.info("Изменение фильма {}", film);

        if(films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }else{
            log.warn("Нет такого фильма!");
            throw new NotFoundException("Нет такого фильма!");
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(Integer id) {
        if(films.containsKey(id)) {
            return films.get(id);
        }else{
            log.warn("Нет такого фильма!");
            throw new NotFoundException("Нет такого фильма!");
        }
    }
}
