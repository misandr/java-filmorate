package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    List<Film> getFilms();
    Film getFilm(Integer id);
}
