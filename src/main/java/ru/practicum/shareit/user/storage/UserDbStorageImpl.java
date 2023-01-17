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
    private final Set<String> emailSet;
    private Long counter;

    public UserDbStorageImpl() {
        this.userMap = new HashMap<>();
        this.emailSet = new HashSet<>();
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
        if (user.getEmail() == null) {
            user.setEmail(user.getName() + "@user.com");
        }
        if (emailSet.contains(user.getEmail())) {
            throw new DuplicateEmailException("Почта уже используется");
        }
        user.setId(counter);
        userMap.put(counter, user);
        counter++;
        emailSet.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(Long userId, User userNew) {
        if (emailSet.contains(userNew.getEmail())) {
            throw new DuplicateEmailException("Почта уже используется");
        }
        User user = userMap.get(userId);
        String email = user.getEmail();
        if (userNew.getName() != null) {
            user.setName(userNew.getName());
        }
        if (userNew.getEmail() != null) {
            user.setEmail(userNew.getEmail());
            emailSet.remove(email);
            emailSet.add(userNew.getEmail());
        }
        userMap.put(userId, user);
        return user;
    }

    @Override
    public void deleteUserById(Long userId) {
        emailSet.remove(userMap.get(userId).getEmail());
        userMap.remove(userId);
    }

}
