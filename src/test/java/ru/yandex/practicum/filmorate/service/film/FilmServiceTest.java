package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmServiceTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;

    @Test
    void getPopularFilms() {

        User[] users = new User[14];
        for(int i = 0; i < users.length; i++) {
            users[i] = createUser("User" + i, "Name", "mail@ya.ru","2000-12-28");
        }

        Film[] films = new Film[15];

        for(int i = 0; i < films.length; i++) {
            films[i] = createFilm("Фильм" + i, "Комедия", "1895-12-28", 60);
        }

        addLikes(films[0], users,2);
        addLikes(films[1], users,5);
        addLikes(films[2], users,12);
        addLikes(films[3], users,2);
        addLikes(films[4], users,11);

        addLikes(films[5], users,0);
        addLikes(films[6], users,5);
        addLikes(films[7], users,10);
        addLikes(films[8], users,6);
        addLikes(films[9], users,9);

        addLikes(films[10], users,3);
        addLikes(films[11], users,8);
        addLikes(films[12], users,6);
        addLikes(films[13], users,1);
        addLikes(films[14], users,13);

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
        film.setMpa(new Mpa(1));

        return filmStorage.addFilm(film);
    }

    User createUser(String login, String name, String email, String birthday) {

        User user = new User();
        user.setId(0);
        user.setLogin(login);
        user.setName(name);
        user.setEmail(email);
        user.setBirthday(birthday);

        return userStorage.addUser(user);
    }

    void addLikes(Film film, User[] users, int countLikes) {
        for(int i = 0; i < countLikes; i++) {
            filmService.addLike(film.getId(), users[i].getId());
        }
    }
}
