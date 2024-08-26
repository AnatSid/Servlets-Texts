package org.example.servletsHomework.storage;

import org.example.servletsHomework.model.User;

import java.util.*;

public class TokensAndUserStorage {

    private final Map<String, User> tokenAndUserMap = new HashMap<>();

    private static final long TOKEN_LIFETIME = 3L * 60 * 1000;

    private long idCounter = 1;

    public User createNewUser(String username, String password) {
        return new User(username, password, idCounter++, 0);
    }

    public boolean ifUserExists(String username) {
        return tokenAndUserMap.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    public void addToken(String token, User user) {
        if (ifTokenExists(token)) {
            return;
        }
        tokenAndUserMap.put(token, user);
    }

    public boolean isTokenValid(String token) {
        if (!ifTokenExists(token)) {
            return false;
        } else {
            User user = tokenAndUserMap.get(token);
            if (user == null)
                return false;

            Long creationTime = user.getCreationTime();
            long currentTime = System.currentTimeMillis();

            if (creationTime == null)
                return false;

            return (currentTime - creationTime) <= TOKEN_LIFETIME;
        }

    }

    public User removeToken(String token) {
        return tokenAndUserMap.remove(token);
    }

    public boolean ifTokenExists(String token) {
        return tokenAndUserMap.containsKey(token);
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public long getUserIdByToken(String token) {
        return tokenAndUserMap.get(token).getId();
    }

}
