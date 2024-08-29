package org.example.servletsHomework.servlet;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.servletsHomework.model.User;
import org.example.servletsHomework.storage.TokensAndUserStorage;

import java.io.IOException;

public class LoginServlet extends HttpServlet {

    private final TokensAndUserStorage tokensAndUserStorage;

    public LoginServlet(TokensAndUserStorage tokensAndUserStorage) {
        this.tokensAndUserStorage = tokensAndUserStorage;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String oldToken = req.getHeader("token");

        if (tokensAndUserStorage.isTokenExists(oldToken)) {

            String newToken = tokensAndUserStorage.generateToken();
            User currentUser = tokensAndUserStorage.removeToken(oldToken);
            currentUser.setLastLoginTime(System.currentTimeMillis());

            tokensAndUserStorage.addToken(newToken, currentUser);

            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            resp.getWriter().write("LoginServlet: new token: " + newToken);

        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("LoginServlet: token not found. Need register");
        }

    }
}