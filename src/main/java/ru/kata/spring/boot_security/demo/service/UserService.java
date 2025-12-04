package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    void updateUserById(User user);

    void saveUser(User user);

    void deleteUserById(Long id);

    User findByUserName(String username);
}
