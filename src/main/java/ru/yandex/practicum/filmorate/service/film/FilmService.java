package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmServiceDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmServiceDao filmServiceDao;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmServiceDao filmServiceDao){
        this.filmStorage = filmStorage;
        this.filmServiceDao = filmServiceDao;
    }

    public void addLike(Integer id, Integer userId){
        filmServiceDao.addLike(id, userId);
    }

    public void removeLike(Integer id, Integer userId){
        filmServiceDao.removeLike(id, userId);
    }

    public List<Film> getPopularFilms(int count){
        return filmServiceDao.getPopularFilms(count);
    }
}
