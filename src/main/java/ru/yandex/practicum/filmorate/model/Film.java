package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film implements Comparable<Film>{
    private int id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    @NotBlank
    private String releaseDate;
    @PositiveOrZero
    private int duration;

    private Set<Integer> likes;

    public Film(){
        likes = new HashSet<>();
    }
    public void addLike(Integer userId) {
        likes.add(userId);
    }
    public void removeLike(Integer userId) {
        if(likes.contains(userId)) {
            likes.remove(userId);
        }else{
            throw new NotFoundException("Не найден userId!");
        }
    }

    public int getCountLikes(){
        return likes.size();
    }

    @Override
    public int compareTo(Film film) {
        if(film.getCountLikes() > getCountLikes()) {
            return 1;
        }else{
            return -1;
        }
    }
}
