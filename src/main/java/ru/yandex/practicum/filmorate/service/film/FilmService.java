package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage){
        this.filmStorage = filmStorage;
    }

    public Film addLike(Integer id, Integer userId){
        Film film = filmStorage.getFilm(id);

        if(film != null){
            film.addLike(userId);

            return film;
        }else{
            throw new NotFoundException("Не найден фильм!");
        }
    }

    public Film removeLike(Integer id, Integer userId){
        Film film = filmStorage.getFilm(id);

        if(film != null){
            film.removeLike(userId);

            return film;
        }else{
            throw new NotFoundException("Не найден фильм!");
        }
    }

    public List<Film> getPopularFilms(int count){
        List<Film> popularFilms = new ArrayList<>();
        TreeSet<Film> sortedFilms = new TreeSet<>(filmStorage.getFilms());

        int i = 0;
        for(Film film: sortedFilms){
            popularFilms.add(film);
            i++;
            if(i == count){
                break;
            }
        }

        return popularFilms;
    }
}
