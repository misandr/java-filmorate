package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    List<Film> getFilms();
    Film getFilm(Integer id);

    List<Genre> getGenres();
    Genre getGenre(Integer id);

    List<Mpa> getMpas();
    Mpa getMpa(Integer id);
}
