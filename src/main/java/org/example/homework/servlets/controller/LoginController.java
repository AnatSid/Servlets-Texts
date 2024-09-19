package org.example.homework.servlets.controller;


import org.example.homework.servlets.model.Token;
import org.example.homework.servlets.storage.TokenStorage;
import org.example.homework.servlets.storage.UserStorage;
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

    private final TokenStorage tokenStorage;
    private final UserStorage userStorage;

    @Autowired
    public LoginController(TokenStorage tokenStorage, UserStorage userStorage) {
        this.tokenStorage = tokenStorage;
        this.userStorage = userStorage;
    }

    @PostMapping()
    public ResponseEntity<String> loginUser(@RequestHeader("username") String username,
                                            @RequestHeader("password") String password) {

        if (userStorage.isUserValid(username,password)) {
            Long userId = userStorage.getUserByUsername(username).getId();
            Token newToken = tokenStorage.createToken(userId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(newToken.getValue());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("invalid login or password");
        }
    }
}