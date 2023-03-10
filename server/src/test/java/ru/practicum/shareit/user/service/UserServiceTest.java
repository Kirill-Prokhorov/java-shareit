package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ExistsElementException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Autowired
    private final UserService userService;
    static User user1 = new User(1L, "user1", "user1@mail.ru");
    static User user2 = new User(2L, "user2", "user2@mail.ru");
    static User user3 = new User(3L, "user3", "user3@mail.ru");
    static User user4 = new User(4L, "user4", "user3@mail.ru");

    @Autowired
    public UserServiceTest(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        this.userService = userService;
    }

    @Test
    void getUserByIdTest() {
        assertEquals(UserMapper.toUserDto(user1).getId(), userService.getUserById(1L).getId());
    }

    @Test
    void retrieveAllUsersTest() {
        assertEquals(3, userService.retrieveAllUsers().size());
    }

    @Test
    void removeUserByIdTest() {
        userService.removeUserById(1L);
        assertNull(userRepository.findById(1L).orElse(null));
    }

    @Test
    void updateUserTest() {
        UserDto userDto = userService.getUserById(1L);
        userDto.setName("newName");
        userDto.setEmail("newmail@test.ru");
        userService.updateUser(1L, userDto);
        assertEquals("newName", userService.getUserById(1L).getName());
        assertEquals("newmail@test.ru", userService.getUserById(1L).getEmail());
    }

    @Test
    void createUserTest() {
        UserDto newUser = userService.createUser(UserMapper.toUserDto(user3));
        assertEquals(userService.getUserById(newUser.getId()).getId(), user3.getId());
    }

    @Test
    void createDoubleUserTest() {
        userService.createUser(UserMapper.toUserDto(user3));
        assertThrows(ExistsElementException.class, () -> userService.createUser(UserMapper.toUserDto(user4)));
    }

}