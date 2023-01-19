package ru.practicum.shareit.user.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getUserDtoById(@PathVariable Long userId) {
        return UserMapper.userToDto(userService.getUserDtoById(userId));
    }

    @GetMapping
    public List<UserDto> getAllUsersDto() {
        return userService.getAllUsersDto()
                .stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return UserMapper.userToDto(userService.createUser(userDto));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return UserMapper.userToDto(userService.updateUser(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }

}