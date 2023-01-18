package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDbStorage;

import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    private final UserDbStorage userDbStorage;

    @Autowired
    public UserServiceImpl(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    @Override
    public User getUserDtoById(Long userId) {
        return userDbStorage.getUserById(userId);
    }

    @Override
    public List<User> getAllUsersDto() {
        return userDbStorage.getAllUsers();

    }

    @Override
    public User createUser(UserDto userDto) {
        return userDbStorage.createUser(UserMapper.fromDtoToUser(userDto));
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = UserMapper.fromDtoToUser(userDto);
        return userDbStorage.updateUser(userId, user);
    }

    @Override
    public void deleteUserById(Long userId) {
        userDbStorage.deleteUserById(userId);
    }

}
