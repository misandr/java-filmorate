package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserServiceDao;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserServiceDao userServiceDao;

    @Autowired
    public UserService(@Qualifier("userDbStorage")UserStorage userStorage, UserServiceDao userServiceDao) {
        this.userStorage = userStorage;
        this.userServiceDao = userServiceDao;
    }

    public void addFriend(Integer id, Integer friendId){
        userServiceDao.addFriend(id, friendId);
    }

    public void removeFriend(Integer id, Integer friendId){
        userServiceDao.removeFriend(id, friendId);
    }

    public List<User> getFriends(Integer id) {
        return userServiceDao.getFriends(id);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId){
        return userServiceDao.getCommonFriends(id, otherId);
    }
}
