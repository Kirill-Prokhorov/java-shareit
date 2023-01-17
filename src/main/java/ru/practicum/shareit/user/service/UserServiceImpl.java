package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDbStorage;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private final UserDbStorage userDbStorage;

    @Autowired
    public UserServiceImpl(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userDbStorage.getUserById(userId);
        return UserMapper.userToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDbStorage.getAllUsers()
                .stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userDbStorage.createUser(UserMapper.fromDtoToUser(userDto));
        return UserMapper.userToDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = UserMapper.fromDtoToUser(userDto);
        return UserMapper.userToDto(userDbStorage.updateUser(userId, user));
    }

    @Override
    public void deleteUserById(Long userId) {
        userDbStorage.deleteUserById(userId);
    }

}
