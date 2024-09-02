package org.example.servletsHomework.storage;

import org.example.servletsHomework.exception.NotFoundException;
import org.example.servletsHomework.model.User;

import java.util.*;

public class TokensAndUserStorage {

    private final Map<String, User> tokenAndUserMap = new HashMap<>();

    private static final long TOKEN_LIFETIME = 5L * 1000;

    private long idCounter = 1;

    public User createNewUser(String username, String password) {
        return new User(username, password, idCounter++, System.currentTimeMillis());
    }

    public boolean isUserExists(String username) {
        return tokenAndUserMap.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    public void addToken(String token, User user) {
        if (isTokenExists(token)) {
            return;
        }
        tokenAndUserMap.put(token, user);
    }

    public boolean isTokenValid(String token) {
        if (!isTokenExists(token)) {
            return false;
        } else {
            User user = tokenAndUserMap.get(token);
            if (user == null)
                return false;

            Long creationTime = user.getLastLoginTime();
            long currentTime = System.currentTimeMillis();

            if (creationTime == null)
                return false;

            return (currentTime - creationTime) <= TOKEN_LIFETIME;
        }

    }

    public User removeToken(String token) {
        return tokenAndUserMap.remove(token);
    }

    public boolean isTokenExists(String token) {
        return tokenAndUserMap.containsKey(token);
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public long getUserIdByToken(String token) {
        User user = tokenAndUserMap.get(token);
        if (user == null) {
            throw new NotFoundException("No user found for the given token");
        }
        return user.getId();
    }

}
