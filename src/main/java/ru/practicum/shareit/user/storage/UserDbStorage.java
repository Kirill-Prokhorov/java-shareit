package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
public interface UserDbStorage {
    User getUserById(Long userId);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(Long userId, User userNew);

    void deleteUserById(Long userId);

}
