package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;

    @NotNull
    @NotBlank
    private String login;

    private String name;

    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotNull
    @NotBlank
    private String birthday;

    private Set<Integer> friends;

    public User(){
        friends = new HashSet<>();
    }

    public void addFriend(Integer friendId) {
        friends.add(friendId);
    }

    public void removeFriend(Integer friendId) {
        if(friends.contains(friendId)) {
            friends.remove(friendId);
        }else{
            throw new NotFoundException("Не найден friendId!");
        }
    }
}
