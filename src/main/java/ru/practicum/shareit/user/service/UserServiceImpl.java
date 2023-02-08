package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistsElementException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDbStorage;

import javax.transaction.Transactional;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDbStorage userDbStorage;

    @Override
    public User getUserById(Long userId) {
        return userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
    }

    @Override
    public List<User> getAllUsersDto() {
        return userDbStorage.findAll();

    }

    @Transactional
    public User createUser(UserDto userDto) {

        User user = UserMapper.fromDtoToUser(userDto);
        try {
            User createdUser = userDbStorage.save(user);
            log.info("Пользователь с почтой {} был создан", user.getEmail());
            return createdUser;
        } catch (RuntimeException e) {
            log.warn("Пользователь с почтой {} уже существует", user.getEmail());
            throw new ExistsElementException("Почта уже зарегестрирована");
        }
    }

    @Transactional
    public User updateUser(Long userId, UserDto userDto) {
        User user = UserMapper.fromDtoToUser(userDto);
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        User updatedUser = getUserById(userId);

        String updatedName = user.getName();
        if (updatedName != null && !updatedName.isBlank())
            updatedUser.setName(updatedName);

        String updatedEmail = user.getEmail();

        if (updatedEmail != null && !updatedEmail.isBlank()) {

            if (userDbStorage.findByEmail(updatedEmail).isPresent()) {
                throw new ExistsElementException("Уже существует пользователь с такой почтой");
            }

            updatedUser.setEmail(updatedEmail);
        }
        return updatedUser;
    }

    @Override
    public void deleteUserById(Long userId) {
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));
        userDbStorage.deleteById(userId);
    }

}
