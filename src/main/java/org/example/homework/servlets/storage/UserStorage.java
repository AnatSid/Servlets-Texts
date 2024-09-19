package org.example.homework.servlets.storage;

import org.example.homework.servlets.exception.NotFoundException;
import org.example.homework.servlets.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserStorage {

    private final List<User> users = new ArrayList<>();
    private long idCounter = 1;


    public void createNewUser(String username, String password) {
        users.add(new User(username, password, idCounter++));
    }

    public User getUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public boolean isUserValid(String username, String password) {
        return users.stream()
                .anyMatch(user -> user.getUsername().equals(username) && user.getPassword().equals(password));
    }

    public boolean isUserExists(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }
}
