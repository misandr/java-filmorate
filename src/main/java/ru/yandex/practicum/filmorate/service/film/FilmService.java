package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeDbStorage likeDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       LikeDbStorage likeDbStorage,
                       GenreDbStorage genreDbStorage,
                       MpaDbStorage mpaDbStorage){
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    public void addLike(Integer id, Integer userId){
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);
        if ((user != null) && (film != null)) {
            likeDbStorage.addLike(id, userId);
        } else {
            throw new NotFoundException("Не найден id!");
        }
    }

    public void removeLike(Integer id, Integer userId){
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);
        if ((user != null) && (film != null)) {
            likeDbStorage.removeLike(id, userId);
        } else {
            throw new NotFoundException("Не найден id!");
        }
    }

    public List<Film> getPopularFilms(int count){
        return likeDbStorage.getPopularFilms(count);
    }

    public List<Genre> getGenres() {
        return genreDbStorage.getAll();
    }

    public Genre getGenre(Integer id) {
        return genreDbStorage.getById(id);
    }

    public List<Mpa> getMpas() {
        return mpaDbStorage.getAll();
    }

    public Mpa getMpa(Integer id) {
        return mpaDbStorage.getById(id);
    }
}
