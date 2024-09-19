package org.example.homework.servlets.controller;

import org.example.homework.servlets.storage.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/register")
public class RegisterController {

    private final UserStorage userStorage;

    @Autowired
    public RegisterController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @PostMapping()
    public ResponseEntity<String> registerUser(@RequestHeader String username, @RequestHeader String password) {

        if (userStorage.isUserExists(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("RegisterServlet: username already exists. Need log in");
        } else {
            userStorage.createNewUser(username, password);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        }
    }
}
