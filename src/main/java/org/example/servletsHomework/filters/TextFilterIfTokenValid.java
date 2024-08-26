package org.example.servletsHomework.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.servletsHomework.storage.TokensAndUserStorage;

import java.io.IOException;

public class TextFilterIfTokenValid extends HttpFilter {

    private final TokensAndUserStorage tokensAndUserStorage;

    public TextFilterIfTokenValid(TokensAndUserStorage tokensAndUserStorage) {
        this.tokensAndUserStorage = tokensAndUserStorage;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String token = request.getHeader("token");

        if (token == null || !tokensAndUserStorage.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("FilterIfTokenValid: НЕ авторизован. Токен закончился\n Надо на ЛОГИН\n");

        } else {
            response.getWriter().write("FilterIfTokenValid: Всё окей- можно пользоваться ресурсом\n");
            chain.doFilter(req, res);
        }

    }
}
