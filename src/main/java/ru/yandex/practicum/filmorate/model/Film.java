package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.*;

@Data
public class Film{
    private int id;

    @NotNull
    @NotBlank(message = "Не указано название фильма!")
    private String name;

    @NotNull
    @Size(min = 0, max = 200, message = "Описание фильма превышает 200 символов!")
    private String description;

    @NotNull
    @NotBlank
    private String releaseDate;
    @PositiveOrZero
    private int duration;

    private Mpa mpa;
    private Set<Genre> genres;

    public Film(){
        genres = new TreeSet<>();
    }

    public Genre getGenre(Integer id){
        for (Genre genre : genres)
            if(id == genre.getId())
                return genre;
        return null;
    }

    public void addGenre(Genre genre){
         genres.add(genre);
    }

    public void sortGenres() {
        Set<Genre> sortedGenres = new TreeSet<>(genres);
        genres = sortedGenres;
    }
}
