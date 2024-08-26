package org.example.servletsHomework.servlet;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.servletsHomework.model.User;
import org.example.servletsHomework.storage.TokensAndUserStorage;

import java.io.IOException;

public class RegisterServlet extends HttpServlet {

    private final TokensAndUserStorage tokensAndUserStorage;

    public RegisterServlet(TokensAndUserStorage tokensAndUserStorage) {
        this.tokensAndUserStorage = tokensAndUserStorage;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getHeader("username");
        String password = request.getHeader("password");

        if (tokensAndUserStorage.ifUserExists(username)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("RegisterServlet2: username already exists. Need log in");

        } else {
            User newUser = tokensAndUserStorage.createNewUser(username, password);
            String newToken = tokensAndUserStorage.generateToken();
            newUser.setCreationTime(System.currentTimeMillis());

            tokensAndUserStorage.addToken(newToken, newUser);
            response.setStatus(HttpServletResponse.SC_CREATED);

            response.getWriter().write(
                    "Registration successful.\n" +
                            "Your login: " + username + "\n" +
                            "Your password: " + password + "\n" +
                            "You token: " + newToken);
        }
    }
}
