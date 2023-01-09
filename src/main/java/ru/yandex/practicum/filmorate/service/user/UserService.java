package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendDbStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendDbStorage friendDbStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage")UserStorage userStorage,
                       FriendDbStorage friendDbStorage) {
        this.userStorage = userStorage;
        this.friendDbStorage = friendDbStorage;
    }

    public void addFriend(Integer id, Integer friendId){
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        if ((user != null) && (friend != null)) {
            friendDbStorage.addFriend(id, friendId);
        } else {
            throw new NotFoundException("Не найден id!");
        }
    }

    public void removeFriend(Integer id, Integer friendId){
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        if ((user != null) && (friend != null)) {
            friendDbStorage.removeFriend(id, friendId);
        } else {
            throw new NotFoundException("Не найден id!");
        }
    }

    public List<User> getFriends(Integer id) {
        return friendDbStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId){
        return friendDbStorage.getCommonFriends(id, otherId);
    }
}
