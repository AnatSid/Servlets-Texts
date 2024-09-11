package org.example.homework.servlets.controller;


import org.example.homework.servlets.model.User;
import org.example.homework.servlets.storage.TokensAndUserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/register")
public class RegisterController {

    private final TokensAndUserStorage tokensAndUserStorage;

    @Autowired
    public RegisterController(TokensAndUserStorage tokensAndUserStorage) {
        this.tokensAndUserStorage = tokensAndUserStorage;
    }

    @PostMapping()
    public ResponseEntity<String> registerUser(@RequestHeader String username, @RequestHeader String password) {

        if (tokensAndUserStorage.isUserExists(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("RegisterServlet: username already exists. Need log in");
        } else {
            User newUser = tokensAndUserStorage.createNewUser(username, password);
            String newToken = tokensAndUserStorage.generateToken();
            tokensAndUserStorage.addToken(newToken, newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(newToken);
        }
    }
}
