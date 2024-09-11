package org.example.servletsHomework.controller;


import org.example.servletsHomework.model.User;
import org.example.servletsHomework.storage.TokensAndUserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login")
public class LoginController {

    private final TokensAndUserStorage tokensAndUserStorage;

    @Autowired
    public LoginController(TokensAndUserStorage tokensAndUserStorage) {
        this.tokensAndUserStorage = tokensAndUserStorage;
    }

    @PostMapping()
    public ResponseEntity<String> loginUser(@RequestHeader("token") String oldToken) {

        if (tokensAndUserStorage.isTokenExists(oldToken)) {
            String newToken = tokensAndUserStorage.generateToken();
            User currentUser = tokensAndUserStorage.removeToken(oldToken);
            currentUser.setLastLoginTime(System.currentTimeMillis());
            tokensAndUserStorage.addToken(newToken, currentUser);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(newToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("LoginServlet: token not found. Need register");
        }
    }
}