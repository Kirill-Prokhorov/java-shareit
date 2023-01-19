package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User getUserDtoById(Long userId);

    List<User> getAllUsersDto();

    User createUser(UserDto userDto);

    User updateUser(Long userId, UserDto userDto);

    void deleteUserById(Long userId);

}