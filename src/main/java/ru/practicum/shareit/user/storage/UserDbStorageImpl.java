package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserDbStorageImpl implements UserDbStorage {

    private final Map<Long, User> userMap;
    private Long counter;

    public UserDbStorageImpl() {
        this.userMap = new HashMap<>();
        this.counter = 1L;
    }

    @Override
    public User getUserById(Long userId) {
        User user = userMap.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User createUser(User user) {
        checkDuplicateEmail(user.getEmail());
        user.setId(counter);
        userMap.put(counter, user);
        counter++;
        return user;
    }

    @Override
    public User updateUser(Long userId, User userNew) {
        checkDuplicateEmail(userNew.getEmail());
        User user = userMap.get(userId);
        if (userNew.getName() != null) {
            user.setName(userNew.getName());
        }
        if (userNew.getEmail() != null) {
            user.setEmail(userNew.getEmail());
        }
        userMap.put(userId, user);
        return user;
    }

    @Override
    public void deleteUserById(Long userId) {
        userMap.remove(userId);
    }

    private void checkDuplicateEmail(String email) {
        for (User userFromMap : userMap.values()) {
            if (userFromMap.getEmail().equals(email)) {
                throw new DuplicateEmailException("Почта уже используется");
            }
        }
    }

}
