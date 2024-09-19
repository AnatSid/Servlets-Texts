package org.example.homework.servlets.storage;


import org.example.homework.servlets.exception.NotFoundException;
import org.example.homework.servlets.model.Token;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TokenStorage {

    private final Map<String, Long> tokenToUserIdMap = new HashMap<>();
    private final Map<Long, Token> userIdToTokenMap = new HashMap<>();

    private static final long TOKEN_LIFETIME = 5L * 1000;

    public Token createToken(Long userId) {
        String newTokenValue = UUID.randomUUID().toString();
        Token newToken = new Token(newTokenValue);

        Token oldToken = userIdToTokenMap.put(userId, newToken);
        if (oldToken != null) {
            tokenToUserIdMap.remove(oldToken.getValue());
        }
        tokenToUserIdMap.put(newToken.getValue(), userId);
        return newToken;
    }

    public Long getUserIdByToken(String tokenValue) {
        Long userId = tokenToUserIdMap.get(tokenValue);
        if (userId == null) {
            throw new NotFoundException("No user found for the given token");
        }
        return userId;
    }

    public boolean isTokenValid(String tokenValue) {
        Long userId = tokenToUserIdMap.get(tokenValue);
        if (userId == null) return false;

        Token token = userIdToTokenMap.get(userId);

        long currentTime = System.currentTimeMillis();

        return (currentTime - token.getCreationTime()) <= TOKEN_LIFETIME;
    }

    public boolean isTokenExists(String tokenValue) {
        return tokenToUserIdMap.containsKey(tokenValue);
    }
}
