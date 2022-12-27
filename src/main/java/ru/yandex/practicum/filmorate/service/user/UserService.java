package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;
    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }
    public User addFriend(Integer id, Integer friendId){

        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        if((user != null) && (friend != null)) {
            user.addFriend(friendId);
            friend.addFriend(id);
        }else{
            throw new NotFoundException("Не найден id!");
        }
        return user;
    }

    public User removeFriend(Integer id, Integer friendId){
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        if((user != null) && (friend != null)) {
            user.removeFriend(friendId);
            friend.removeFriend(id);
        }else{
            throw new NotFoundException("Не найден id!");
        }
        return user;
    }

    public List<User> getFriends(Integer id) {
        ArrayList<User> friends = new ArrayList<>();

        User user = userStorage.getUser(id);

        if(user != null) {
            Set<Integer> friendIds = user.getFriends();

            for(Integer friendId: friendIds){
                friends.add(userStorage.getUser(friendId));
            }
        }

        return friends;
    }

    public List<User> getCommonFriends(Integer id, Integer otherId){
        ArrayList<User> commonFriends = new ArrayList<>();

        User user1 = userStorage.getUser(id);
        User user2 = userStorage.getUser(otherId);

        if((user1 != null) && (user2 != null)) {
            Set<Integer> friendsUser1 = user1.getFriends();
            Set<Integer> friendsUser2 = user2.getFriends();

            if((friendsUser1 != null) && (friendsUser2 != null)) {
                for (Integer friendId : friendsUser1) {
                    if (friendsUser2.contains(friendId)) {
                        commonFriends.add(userStorage.getUser(friendId));
                    }
                }
            }
        }

        return commonFriends;
    }
}
